package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Course;
import com.awbd.online_learning.domain.Module;
import com.awbd.online_learning.dtos.ModuleDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.CourseRepository;
import com.awbd.online_learning.repository.ModuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceImplTest_Unit {

    @Mock
    private ModuleRepository moduleRepository;
    @Mock
    private CourseRepository courseRepository;
    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private ModuleServiceImpl moduleService;

    private Course course;
    private Module module;
    private ModuleDTO moduleDTO;

    @BeforeEach
    void setUp() {
        course = Course.builder().id(10L).title("Parent Course").build();
        module = Module.builder().id(1L).title("First Module").course(course).build();
        moduleDTO = ModuleDTO.builder().id(1L).title("First Module").courseId(10L).build();
    }

    @Test
    void findByCourseId_whenCourseExists_shouldReturnModuleList() {
        // Given
        when(courseRepository.existsById(10L)).thenReturn(true);
        when(moduleRepository.findByCourseId(10L)).thenReturn(Collections.singletonList(module));

        // When
        List<ModuleDTO> result = moduleService.findByCourseId(10L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("First Module");
        verify(moduleRepository).findByCourseId(10L);
    }

    @Test
    void findByCourseId_whenCourseDoesNotExist_shouldThrowException() {
        // Given
        when(courseRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> moduleService.findByCourseId(99L));
        verify(moduleRepository, never()).findByCourseId(anyLong());
    }

    @Test
    void save_whenCourseExists_shouldSaveAndReturnModuleDTO() {
        // Given
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        // When
        ModuleDTO savedDTO = moduleService.save(moduleDTO);

        // Then
        assertNotNull(savedDTO);
        assertEquals(10L, savedDTO.getCourseId());
        verify(courseRepository).findById(10L);
        verify(moduleRepository).save(any(Module.class));
    }
}
