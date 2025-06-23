package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Course;
import com.awbd.online_learning.domain.Instructor;
import com.awbd.online_learning.dtos.CourseDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.CourseRepository;
import com.awbd.online_learning.repository.InstructorRepository;
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
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CourseDTO> findAll() {
        log.debug("Finding all courses");
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CourseDTO> findAll(Pageable pageable) {
        log.debug("Finding all courses with pagination: {}", pageable);
        return courseRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public CourseDTO findById(Long id) {
        log.debug("Attempting to find course with id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Course not found with id: {}", id);
                    return new ResourceNotFoundException("Course not found with id: " + id);
                });
        return convertToDTO(course);
    }

    @Override
    @Transactional
    public CourseDTO save(CourseDTO courseDTO) {
        String action = courseDTO.getId() == null ? "Creating new" : "Updating";
        log.debug("{} course with title: '{}'", action, courseDTO.getTitle());
        // find the instructor for this course
        Instructor instructor = instructorRepository.findById(courseDTO.getInstructorId())
                .orElseThrow(() -> {
                    log.error("Cannot save course. Instructor not found with id: {}", courseDTO.getInstructorId());
                    return new ResourceNotFoundException("Instructor not found with id: " + courseDTO.getInstructorId());
                });

        Course course = convertToEntity(courseDTO);
        course.setInstructor(instructor); // set the found instructor

        Course savedCourse = courseRepository.save(course);
        log.info("Successfully saved course '{}' with ID: {}", savedCourse.getTitle(), savedCourse.getId());
        return convertToDTO(savedCourse);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Attempting to delete course with id: {}", id);
        if (!courseRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent course with id: {}", id);
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
        log.info("Successfully deleted course with id: {}", id);
    }

    // helper methods

    private CourseDTO convertToDTO(Course course) {
        CourseDTO courseDTO = modelMapper.map(course, CourseDTO.class);
        if (course.getInstructor() != null) {
            courseDTO.setInstructorId(course.getInstructor().getId());
            courseDTO.setInstructorName(course.getInstructor().getFirstName() + " " + course.getInstructor().getLastName());
        }
        return courseDTO;
    }

    private Course convertToEntity(CourseDTO courseDTO) {
        return modelMapper.map(courseDTO, Course.class);
    }
}
