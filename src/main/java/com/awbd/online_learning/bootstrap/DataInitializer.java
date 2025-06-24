package com.awbd.online_learning.bootstrap;

import com.awbd.online_learning.domain.Authority;
import com.awbd.online_learning.domain.Instructor;
import com.awbd.online_learning.domain.User;
import com.awbd.online_learning.repository.AuthorityRepository;
import com.awbd.online_learning.repository.InstructorRepository;
import com.awbd.online_learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("prod") // this runner will only execute when the 'prod' profile is active
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization for production environment...");


        Authority adminRole = createAuthorityIfNotFound("ROLE_ADMIN");
        Authority instructorRole = createAuthorityIfNotFound("ROLE_INSTRUCTOR");
        Authority studentRole = createAuthorityIfNotFound("ROLE_STUDENT");


        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin_password"))
                    .authorities(Set.of(adminRole))
                    .enabled(true)
                    .build();
            userRepository.save(adminUser);
            log.info("Created ADMIN user: 'admin'");
        }


        if (instructorRepository.findByEmail("prof.xavier@school.com").isEmpty()) {

            Instructor instructor = Instructor.builder()
                    .firstName("Charles")
                    .lastName("Xavier")
                    .email("prof.xavier@school.com")
                    .bio("a powerful mind and a leader of mutants")
                    .build();
            Instructor savedInstructor = instructorRepository.save(instructor);


            User instructorUser = User.builder()
                    .username("xavier")
                    .password(passwordEncoder.encode("instructor_pass"))
                    .authorities(Set.of(instructorRole))
                    .enabled(true)
                    .build();
            userRepository.save(instructorUser);

            log.info("Created INSTRUCTOR: '{}' with user: '{}'", savedInstructor.getEmail(), instructorUser.getUsername());
        }

        log.info("Data initialization complete.");
    }

    private Authority createAuthorityIfNotFound(String name) {
        return authorityRepository.findByName(name)
                .orElseGet(() -> {
                    log.info("Creating authority: {}", name);
                    return authorityRepository.save(Authority.builder().name(name).build());
                });
    }
}
