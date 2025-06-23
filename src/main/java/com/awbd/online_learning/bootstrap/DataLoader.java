package com.awbd.online_learning.bootstrap;

import com.awbd.online_learning.domain.Authority;
import com.awbd.online_learning.domain.User;
import com.awbd.online_learning.repository.AuthorityRepository;
import com.awbd.online_learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Important!

import java.util.Set;

@Component
@Profile("dev")
// Or remove @Profile to run always if users table is empty
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (authorityRepository.count() == 0) {
            loadSecurityData();
        }

    }

    private void loadSecurityData() {

        Authority roleAdmin = authorityRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_ADMIN").build()));

        Authority roleInstructor = authorityRepository.findByName("ROLE_INSTRUCTOR")
                .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_INSTRUCTOR").build()));

        Authority roleStudent = authorityRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_STUDENT").build()));

        // Create Users
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("adminpass"))
                    .enabled(true)
                    .authorities(Set.of(roleAdmin, roleStudent)) // Admin can also be student
                    .build();
            userRepository.save(adminUser);
            System.out.println("Loaded Admin User.");
        }

        if (userRepository.findByUsername("instructor1").isEmpty()) {
            User instructorUser = User.builder()
                    .username("instructor1")
                    .password(passwordEncoder.encode("instrpass"))
                    .enabled(true)
                    .authorities(Set.of(roleInstructor, roleStudent)) // Instructor can also be a student
                    .build();
            userRepository.save(instructorUser);
            System.out.println("Loaded Instructor User.");
        }

        if (userRepository.findByUsername("student1").isEmpty()) {
            User studentUser = User.builder()
                    .username("student1")
                    .password(passwordEncoder.encode("studpass"))
                    .enabled(true)
                    .authorities(Set.of(roleStudent))
                    .build();
            userRepository.save(studentUser);
            System.out.println("Loaded Student User.");
        }
    }
}