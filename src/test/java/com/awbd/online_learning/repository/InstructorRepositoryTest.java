package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Instructor;
import com.awbd.online_learning.domain.InstructorProfile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
public class InstructorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // helper for persisting entities in tests

    @Autowired
    private InstructorRepository instructorRepository;

    @Test
    void whenFindById_thenReturnInstructor() {
        // Given
        Instructor instructor = Instructor.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .bio("Test bio")
                .build();
        entityManager.persist(instructor);
        entityManager.flush(); // force a flush to the DB

        // When
        Optional<Instructor> found = instructorRepository.findById(instructor.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(instructor.getEmail());
    }

    @Test
    void whenSaveInstructorWithProfile_thenProfileIsSaved() {
        // Given
        InstructorProfile profile = InstructorProfile.builder()
                .website("johndoe.com")
                .yearsOfExperience(5)
                .build();

        Instructor instructor = Instructor.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .bio("Another bio")

                .build();
        instructor.setInstructorProfile(profile);

        // When
        Instructor savedInstructor = instructorRepository.save(instructor);
        entityManager.flush(); // ensure data is written
        entityManager.clear(); // clear persistence context to force reload from DB

        // Then
        Optional<Instructor> foundInstructorOpt = instructorRepository.findById(savedInstructor.getId());
        assertTrue(foundInstructorOpt.isPresent());
        Instructor foundInstructor = foundInstructorOpt.get();

        assertNotNull(foundInstructor.getProfile());
        assertEquals("johndoe.com", foundInstructor.getProfile().getWebsite());
        assertEquals(instructor.getId(), foundInstructor.getProfile().getInstructor().getId()); // check back-reference
    }

    @Test
    void whenFindByNonExistentId_thenReturnEmpty() {
        // When
        Optional<Instructor> found = instructorRepository.findById(-99L); // A non-existent ID

        // Then
        assertThat(found).isNotPresent();
    }
}