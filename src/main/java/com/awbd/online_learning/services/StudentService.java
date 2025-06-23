package com.awbd.online_learning.services;

import com.awbd.online_learning.dtos.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    List<StudentDTO> findAll();
    Page<StudentDTO> findAll(Pageable pageable);
    StudentDTO findById(Long id);
    StudentDTO save(StudentDTO studentDTO);
    void deleteById(Long id);
}
