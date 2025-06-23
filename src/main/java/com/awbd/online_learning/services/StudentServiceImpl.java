package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Student;
import com.awbd.online_learning.dtos.StudentDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<StudentDTO> findAll() {
        log.debug("Finding all students");
        return studentRepository.findAll().stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentDTO> findAll(Pageable pageable) {
        log.debug("Finding all students with pagination: {}", pageable);
        return studentRepository.findAll(pageable)
                .map(student -> modelMapper.map(student, StudentDTO.class));
    }

    @Override

    public StudentDTO findById(Long id) {
        log.debug("Attempting to find student with id: {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Student not found with id: {}", id);
                    return new ResourceNotFoundException("Student not found with id: " + id);
                });
        return modelMapper.map(student, StudentDTO.class);
    }

    @Override
    @Transactional
    public StudentDTO save(StudentDTO studentDTO) {
        String action = studentDTO.getId() == null ? "Creating new" : "Updating";
        log.debug("{} student with email: {}", action, studentDTO.getEmail());

        Student student = modelMapper.map(studentDTO, Student.class);
        Student savedStudent = studentRepository.save(student);

        log.info("Successfully saved student with ID: {}", savedStudent.getId());
        return modelMapper.map(savedStudent, StudentDTO.class);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Attempting to delete student with id: {}", id);
        if (!studentRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent student with id: {}", id);
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
        log.info("Successfully deleted student with id: {}", id);
    }
}
