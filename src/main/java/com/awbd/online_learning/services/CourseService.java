package com.awbd.online_learning.services;

import com.awbd.online_learning.dtos.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    List<CourseDTO> findAll();
    Page<CourseDTO> findAll(Pageable pageable);
    CourseDTO findById(Long id);
    CourseDTO save(CourseDTO courseDTO);
    void deleteById(Long id);
}
