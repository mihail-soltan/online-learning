package com.awbd.online_learning.services;

import com.awbd.online_learning.dtos.InstructorDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InstructorService {
    List<InstructorDTO> findAll();
    Page<InstructorDTO> findAll(Pageable pageable);
    InstructorDTO findById(Long id);
    InstructorDTO save(InstructorDTO instructorDTO);
    void deleteById(Long id);
}