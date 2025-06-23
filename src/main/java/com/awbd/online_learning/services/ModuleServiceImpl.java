package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Course;
import com.awbd.online_learning.domain.Module;
import com.awbd.online_learning.dtos.ModuleDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.CourseRepository;
import com.awbd.online_learning.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ModuleDTO> findByCourseId(Long courseId) {
        log.debug("Finding all modules for courseId: {}", courseId);
        if (!courseRepository.existsById(courseId)) {
            log.error("Attempted to find modules for a non-existent course with id: {}", courseId);
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return moduleRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ModuleDTO findById(Long id) {
        log.debug("Attempting to find module with id: {}", id);
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Module not found with id: {}", id);
                    return new ResourceNotFoundException("Module not found with id: " + id);
                });
        return convertToDTO(module);
    }

    @Override
    @Transactional
    public ModuleDTO save(ModuleDTO moduleDTO) {
        String action = moduleDTO.getId() == null ? "Creating new" : "Updating";
        log.debug("{} module with title '{}' for courseId: {}", action, moduleDTO.getTitle(), moduleDTO.getCourseId());
        Course course = courseRepository.findById(moduleDTO.getCourseId())
                .orElseThrow(() -> {
                    log.error("Cannot save module. Course not found with id: {}", moduleDTO.getCourseId());
                    return new ResourceNotFoundException("Course not found with id: " + moduleDTO.getCourseId());
                });

        Module module = modelMapper.map(moduleDTO, Module.class);
        module.setCourse(course); // associate with the course

        Module savedModule = moduleRepository.save(module);
        log.info("Successfully saved module '{}' with ID: {}", savedModule.getTitle(), savedModule.getId());
        return convertToDTO(savedModule);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Attempting to delete module with id: {}", id);
        if (!moduleRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent module with id: {}", id);
            throw new ResourceNotFoundException("Module not found with id: " + id);
        }
        moduleRepository.deleteById(id);
        log.info("Successfully deleted module with id: {}", id);
    }

    // helper methods

    private ModuleDTO convertToDTO(Module module) {
        ModuleDTO dto = modelMapper.map(module, ModuleDTO.class);
        if (module.getCourse() != null) {
            dto.setCourseId(module.getCourse().getId());
        }
        return dto;
    }
}
