package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Course;
import com.awbd.online_learning.domain.Instructor;
import com.awbd.online_learning.domain.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ModuleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ModuleRepository moduleRepository;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        Instructor instructor = entityManager.persist(Instructor.builder().firstName("Inst").build());
        course1 = entityManager.persist(Course.builder().title("Course 1").instructor(instructor).build());
        course2 = entityManager.persist(Course.builder().title("Course 2").instructor(instructor).build());

        entityManager.persist(Module.builder().title("M1 C1").course(course1).build());
        entityManager.persist(Module.builder().title("M2 C1").course(course1).build());
        entityManager.persist(Module.builder().title("M1 C2").course(course2).build());
        entityManager.flush();
    }

    @Test
    void findByCourseId_shouldReturnOnlyModulesForThatCourse() {
        // When
        List<Module> course1Modules = moduleRepository.findByCourseId(course1.getId());
        List<Module> course2Modules = moduleRepository.findByCourseId(course2.getId());

        // Then
        assertThat(course1Modules).hasSize(2);
        assertThat(course1Modules).extracting(Module::getTitle).containsExactlyInAnyOrder("M1 C1", "M2 C1");

        assertThat(course2Modules).hasSize(1);
        assertThat(course2Modules.get(0).getTitle()).isEqualTo("M1 C2");
    }
}
