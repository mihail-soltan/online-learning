package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Course;
import com.awbd.online_learning.domain.Instructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    private Instructor instructor;

    @BeforeEach
    void setUp() {
        // create and persist a reusable instructor for the courses
        instructor = Instructor.builder()
                .firstName("Shared")
                .lastName("Instructor")
                .email("shared.instructor@example.com")
                .build();
        entityManager.persist(instructor);
    }

    @Test
    void whenSaveCourse_thenCourseIsPersisted() {
        // Given
        Course course = Course.builder()
                .title("New Course")
                .description("A great new course.")
                .price(99.99)
                .instructor(instructor)
                .build();

        // When
        Course savedCourse = courseRepository.save(course);

        // Then
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getInstructor().getFirstName()).isEqualTo("Shared");
    }

    @Test
    void whenFindById_thenReturnCourse() {
        // Given
        Course course = Course.builder().title("Find Me").description("Desc").price(10.0).instructor(instructor).build();
        entityManager.persist(course);
        entityManager.flush();

        // When
        Optional<Course> found = courseRepository.findById(course.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Find Me");
    }

    @Test
    void whenDeleteCourse_thenItIsRemoved() {
        // Given
        Course course = Course.builder().title("To Be Deleted").description("...").price(0.0).instructor(instructor).build();
        entityManager.persist(course);
        entityManager.flush();
        Long courseId = course.getId();

        // When
        courseRepository.deleteById(courseId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Course> found = courseRepository.findById(courseId);
        assertThat(found).isNotPresent();
    }
}
