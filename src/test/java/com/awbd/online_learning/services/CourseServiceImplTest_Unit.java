package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Course;
import com.awbd.online_learning.domain.Instructor;
import com.awbd.online_learning.dtos.CourseDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.CourseRepository;
import com.awbd.online_learning.repository.InstructorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class CourseServiceImplTest_Unit {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private InstructorRepository instructorRepository;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private Instructor instructor;
    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        // create and configure ModelMapper INSTANCE
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Course.class, CourseDTO.class).addMappings(mapper -> {
            mapper.skip(CourseDTO::setInstructorName); // Crucial for ambiguity
        });

        // instantiate the service under test, injecting the configured modelMapper and mocks
        courseService = new CourseServiceImpl(courseRepository, instructorRepository, modelMapper);

        // setup test data
        instructor = Instructor.builder().id(10L).firstName("Prof").lastName("X").build();
        course = Course.builder().id(1L).title("Test Course").description("A test course").price(99.99).instructor(instructor).build();
        courseDTO = CourseDTO.builder().id(1L).title("Test Course").description("A test course DTO").price(99.99).instructorId(10L).instructorName("Prof X").build();
    }

    @Test
    void findById_whenCourseExists_shouldReturnCourseDTO() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // When
        CourseDTO foundDTO = courseService.findById(1L);

        // Then
        assertNotNull(foundDTO);
        assertEquals("Test Course", foundDTO.getTitle());
        assertEquals(10L, foundDTO.getInstructorId());
        assertEquals("Prof X", foundDTO.getInstructorName()); // check name is set
        verify(courseRepository).findById(1L);
    }

    @Test
    void save_whenInstructorExists_shouldSaveAndReturnCourseDTO() {
        // Given
        // create a DTO for saving (could be new or update)
        CourseDTO dtoToSave = CourseDTO.builder()
                .title("New Course Title")
                .description("New Desc")
                .price(149.99)
                .instructorId(10L)
                .build();

        // entity that would be created from dtoToSave (instructor is set by service)
        Course courseToSave = Course.builder()
                .title("New Course Title")
                .description("New Desc")
                .price(149.99)
                // instructor will be set inside the service
                .build();

        // Entity that would be returned after saving (with an ID and instructor)
        Course savedCourseEntity = Course.builder()
                .id(5L) // Simulate DB generated ID
                .title("New Course Title")
                .description("New Desc")
                .price(149.99)
                .instructor(instructor)
                .build();


        when(instructorRepository.findById(10L)).thenReturn(Optional.of(instructor));
        when(courseRepository.save(any(Course.class))).thenReturn(savedCourseEntity);

        // When
        CourseDTO resultDTO = courseService.save(dtoToSave);

        // Then
        assertNotNull(resultDTO);
        assertEquals("New Course Title", resultDTO.getTitle());
        assertEquals(5L, resultDTO.getId()); // ID from savedCourseEntity
        assertEquals(10L, resultDTO.getInstructorId()); // Instructor ID
        assertEquals("Prof X", resultDTO.getInstructorName()); // Instructor Name

        verify(instructorRepository).findById(10L);
        verify(courseRepository).save(any(Course.class)); // check that an entity was passed to save
    }

    @Test
    void save_whenInstructorDoesNotExist_shouldThrowResourceNotFoundException() {
        // Given
        when(instructorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.save(courseDTO);
        });

        //verify that save was never called because it failed before that
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void deleteById_shouldCallRepositoryDelete() {
        // Given
        when(courseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(courseRepository).deleteById(1L);

        // When
        courseService.deleteById(1L);

        // Then
        verify(courseRepository).deleteById(1L);
    }
}
