package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Authority;
import com.awbd.online_learning.domain.User;
import com.awbd.online_learning.dtos.UserDTO;
import com.awbd.online_learning.exceptions.ResourceExistsException;
import com.awbd.online_learning.repository.AuthorityRepository;
import com.awbd.online_learning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registerUser(UserDTO userDTO) {
        log.debug("Attempting to register new user with username: {}", userDTO.getUsername());
        userRepository.findByUsername(userDTO.getUsername()).ifPresent(u -> {
            log.warn("Registration failed: username '{}' already exists.", u.getUsername());
            throw new ResourceExistsException("User with username " + u.getUsername() + " already exists.");
        });

        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setEnabled(true);

        // find or create default role (ROLE_STUDENT)
        Authority defaultAuthority = authorityRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> authorityRepository.save(Authority.builder().name("ROLE_STUDENT").build()));

        Set<Authority> authorities = new HashSet<>();
        authorities.add(defaultAuthority);
        newUser.setAuthorities(authorities);

        userRepository.save(newUser);
        log.info("Successfully registered new user with username: '{}'", newUser.getUsername());
    }
}
