package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void saveAndFindById_shouldWork() {
        // Given
        Student student = Student.builder().firstName("Peter").lastName("Jones").email("peter.j@test.com").build();

        // When
        Student savedStudent = studentRepository.save(student);
        Student foundStudent = studentRepository.findById(savedStudent.getId()).orElse(null);

        // Then
        assertThat(foundStudent).isNotNull();
        assertThat(foundStudent.getEmail()).isEqualTo("peter.j@test.com");
    }
}
