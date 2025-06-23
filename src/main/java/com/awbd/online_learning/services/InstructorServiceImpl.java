package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Instructor;
import com.awbd.online_learning.domain.InstructorProfile;
import com.awbd.online_learning.dtos.InstructorDTO;
import com.awbd.online_learning.exceptions.ResourceNotFoundException;
import com.awbd.online_learning.repository.InstructorProfileRepository;
import com.awbd.online_learning.repository.InstructorRepository;
import lombok.RequiredArgsConstructor; // For constructor injection
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Important for operations spanning multiple saves or entities

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<InstructorDTO> findAll() {
        return instructorRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<InstructorDTO> findAll(Pageable pageable) {
        return instructorRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Override
    public InstructorDTO findById(Long id) {
        log.debug("Attempting to find instructor with id: {}", id);
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> {
                        log.error("Instructor not found with id: {}", id);
                        return new ResourceNotFoundException("Instructor not found with id: " + id);
                });
        return convertToDTO(instructor);
    }

    @Override
    @Transactional
    public InstructorDTO save(InstructorDTO instructorDTO) {
        String action = instructorDTO.getId() == null ? "Creating new" : "Updating";
        log.debug("{} instructor with email: {}", action, instructorDTO.getEmail());

        Instructor instructor = convertToEntity(instructorDTO);


        InstructorProfile profile;
        if (instructor.getId() != null && instructor.getProfile() != null) {

            profile = instructorProfileRepository.findById(instructor.getProfile().getId())
                    .orElse(new InstructorProfile());
        } else if (instructor.getProfile() != null) {
            // new or existing instructor, potentially new profile data from DTO
            profile = new InstructorProfile();
        } else {
            profile = null; // No profile data
        }

        if (profile != null) {
            profile.setWebsite(instructorDTO.getWebsite());
            profile.setYearsOfExperience(instructorDTO.getYearsOfExperience());
        }

        log.debug("Attempting to save instructor: {}", instructorDTO.getEmail());
        Instructor savedInstructor = instructorRepository.save(instructor);
        log.info("Successfully saved instructor with ID: {}", savedInstructor.getId());
        return convertToDTO(savedInstructor);
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("Attempting to delete instructor with id: {}", id);
        if (!instructorRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent instructor with id: {}", id);
            throw new ResourceNotFoundException("Instructor not found with id: " + id + " for deletion.");
        }
        instructorRepository.deleteById(id);
        log.info("Successfully deleted instructor with id: {}", id);
    }

    // helper methods for DTO/Entity Conversion

    private InstructorDTO convertToDTO(Instructor instructor) {
        InstructorDTO dto = modelMapper.map(instructor, InstructorDTO.class);
        if (instructor.getProfile() != null) {
            dto.setWebsite(instructor.getProfile().getWebsite());
            dto.setYearsOfExperience(instructor.getProfile().getYearsOfExperience());
        }
        return dto;
    }

    private Instructor convertToEntity(InstructorDTO instructorDTO) {
        Instructor instructor = modelMapper.map(instructorDTO, Instructor.class);

        if (instructorDTO.getId() != null) { // Existing instructor
            Instructor existingInstructor = instructorRepository.findById(instructorDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot update non-existent instructor with id: " + instructorDTO.getId()));

            instructor.setProfile(existingInstructor.getProfile()); // start with existing profile
            instructor.setCourses(existingInstructor.getCourses()); // preserve existing courses
        }


        InstructorProfile profile = instructor.getProfile();
        if (profile == null) {
              if (instructorDTO.getWebsite() != null || instructorDTO.getYearsOfExperience() != null) {
                profile = new InstructorProfile();
            }
        }

        if (profile != null) {
            profile.setWebsite(instructorDTO.getWebsite());
            profile.setYearsOfExperience(instructorDTO.getYearsOfExperience());
            instructor.setInstructorProfile(profile);
        }

        return instructor;
    }
}