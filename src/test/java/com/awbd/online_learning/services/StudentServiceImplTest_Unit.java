package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Student;
import com.awbd.online_learning.dtos.StudentDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest_Unit {

    @Mock
    private StudentRepository studentRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        student = Student.builder().id(1L).firstName("John").lastName("Smith").email("john.smith@example.com").build();
        studentDTO = StudentDTO.builder().id(1L).firstName("John").lastName("Smith").email("john.smith@example.com").build();
    }

    @Test
    void findById_whenStudentExists_shouldReturnStudentDTO() {
        // Given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        // When
        StudentDTO foundDTO = studentService.findById(1L);

        // Then
        assertNotNull(foundDTO);
        assertEquals("John", foundDTO.getFirstName());
        verify(studentRepository).findById(1L);
    }

    @Test
    void findById_whenStudentDoesNotExist_shouldThrowResourceNotFoundException() {
        // Given
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> studentService.findById(99L));
        verify(studentRepository).findById(99L);
    }

    @Test
    void save_shouldReturnSavedStudentDTO() {
        // Given
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentDTO savedDTO = studentService.save(studentDTO);

        // Then
        assertNotNull(savedDTO);
        assertEquals(studentDTO.getEmail(), savedDTO.getEmail());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void deleteById_whenStudentExists_shouldCallRepositoryDelete() {
        // Given
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        // When
        studentService.deleteById(1L);

        // Then
        verify(studentRepository).existsById(1L);
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteById_whenStudentDoesNotExist_shouldThrowException() {
        // Given
        when(studentRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> studentService.deleteById(99L));
        verify(studentRepository, never()).deleteById(anyLong());
    }
}
