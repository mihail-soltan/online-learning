package com.awbd.online_learning.services;

import com.awbd.online_learning.dtos.ModuleDTO;

import java.util.List;

public interface ModuleService {
    List<ModuleDTO> findByCourseId(Long courseId);
    ModuleDTO findById(Long id);
    ModuleDTO save(ModuleDTO moduleDTO);
    void deleteById(Long id);
}
