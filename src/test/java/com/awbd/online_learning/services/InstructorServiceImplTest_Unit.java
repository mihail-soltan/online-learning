package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Instructor;
import com.awbd.online_learning.domain.InstructorProfile;
import com.awbd.online_learning.dtos.InstructorDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.InstructorProfileRepository;
import com.awbd.online_learning.repository.InstructorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper; // Import ModelMapper
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstructorServiceImplTest_Unit {

    @Mock // create a mock implementation for InstructorRepository
    private InstructorRepository instructorRepository;

    @Mock // Mock for InstructorProfileRepository
    private InstructorProfileRepository instructorProfileRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private InstructorServiceImpl instructorService;

    private Instructor instructor;
    private InstructorDTO instructorDTO;

    @BeforeEach
    void setUp() {
        InstructorProfile profile = InstructorProfile.builder().id(1L).website("test.com").yearsOfExperience(2).build();
        instructor = Instructor.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .bio("Bio")
                .profile(profile)
                .build();

        instructorDTO = InstructorDTO.builder()
                .id(1L)
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .bio("Bio")
                .website("test.com")
                .yearsOfExperience(2)
                .build();
    }

    @Test
    void findById_whenInstructorExists_shouldReturnInstructorDTO() {
        // Given
        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        // When
        InstructorDTO foundDTO = instructorService.findById(1L);

        // Then
        assertNotNull(foundDTO);
        assertEquals(instructor.getEmail(), foundDTO.getEmail());
        assertEquals(instructor.getProfile().getWebsite(), foundDTO.getWebsite());
        verify(instructorRepository).findById(1L); // verify repository method was called
    }

    @Test
    void findById_whenInstructorDoesNotExist_shouldThrowResourceNotFoundException() {
        // Given
        when(instructorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> {
            instructorService.findById(1L);
        });
        verify(instructorRepository).findById(1L);
    }

    @Test
    void saveInstructor_shouldReturnSavedInstructorDTO() {
        // Given
        InstructorDTO newDtoToSave = InstructorDTO.builder() // ID is null
                .firstName("New").lastName("Instr").email("new@e.com").bio("NB")
                .website("new.com").yearsOfExperience(1).build();

        // only stub what's definitely called for a new instructor save
        when(instructorRepository.save(any(Instructor.class))).thenAnswer(invocation -> {
            Instructor arg = invocation.getArgument(0);
            arg.setId(2L); // simulate ID generation on save

            return arg;
        });

        // When
        InstructorDTO savedDTO = instructorService.save(newDtoToSave);

        // Then
        assertNotNull(savedDTO);
        assertEquals("New", savedDTO.getFirstName());
        assertNotNull(savedDTO.getId()); // check that an ID was assigned
        verify(instructorRepository).save(any(Instructor.class));
        // verify findById was NOT called for instructor or profile repo for new save
        verify(instructorRepository, never()).findById(anyLong());
        verify(instructorProfileRepository, never()).findById(anyLong());
    }

    @Test
    void findAllPaginated_shouldReturnPageOfInstructorDTO() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<Instructor> instructorPage = new PageImpl<>(Collections.singletonList(instructor), pageable, 1);
        when(instructorRepository.findAll(any(Pageable.class))).thenReturn(instructorPage);

        // When
        Page<InstructorDTO> resultPage = instructorService.findAll(pageable);

        // Then
        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getEmail()).isEqualTo(instructor.getEmail());
        verify(instructorRepository).findAll(pageable);
    }


    @Test
    void saveInstructor_whenUpdatingExisting_shouldReturnUpdatedInstructorDTO() {
        // Given
        InstructorDTO dtoToUpdate = InstructorDTO.builder()
                .id(1L) // existing ID
                .firstName("Updated").lastName("User").email("updated@example.com").bio("Updated Bio")
                .website("updated.com").yearsOfExperience(5).build();

        //for an update, instructorRepository.findById IS called by convertToEntity
        Instructor existingInstructor = Instructor.builder().id(1L).firstName("Old").build();
        existingInstructor.setInstructorProfile(InstructorProfile.builder().id(1L).build());

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(existingInstructor));

        when(instructorProfileRepository.findById(1L)).thenReturn(Optional.of(existingInstructor.getProfile()));

        when(instructorRepository.save(any(Instructor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InstructorDTO updatedDTO = instructorService.save(dtoToUpdate);

        // Then
        assertNotNull(updatedDTO);
        assertEquals("Updated", updatedDTO.getFirstName());
        assertEquals(1L, updatedDTO.getId());
        verify(instructorRepository).findById(1L); // verify findById WAS called
        verify(instructorRepository).save(any(Instructor.class));
    }
}