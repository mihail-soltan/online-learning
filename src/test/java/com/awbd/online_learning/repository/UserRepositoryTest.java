package com.awbd.online_learning.repository;

import com.awbd.online_learning.domain.Authority;
import com.awbd.online_learning.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository; // used to create authorities

    @Test
    void whenFindByUsername_thenReturnUser() {
        // Given
        User user = User.builder()
                .username("testuser")
                .password("password")
                .enabled(true)
                .build();
        entityManager.persist(user);
        entityManager.flush();

        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void whenFindByNonExistentUsername_thenReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(found).isNotPresent();
    }

    @Test
    void whenSaveUserWithAuthorities_thenAuthoritiesAreSaved() {
        // Given
        Authority adminRole = authorityRepository.save(Authority.builder().name("ROLE_ADMIN").build());
        Authority userRole = authorityRepository.save(Authority.builder().name("ROLE_USER").build());

        User user = User.builder()
                .username("multi-role-user")
                .password("securepass")
                .enabled(true)
                .authorities(Set.of(adminRole, userRole))
                .build();

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<User> foundUserOpt = userRepository.findById(savedUser.getId());
        assertThat(foundUserOpt).isPresent();
        User foundUser = foundUserOpt.get();
        assertThat(foundUser.getAuthorities()).hasSize(2);
        assertThat(foundUser.getAuthorities()).extracting(Authority::getName).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }
}
