package com.awbd.online_learning.services;

import com.awbd.online_learning.domain.Authority;
import com.awbd.online_learning.domain.User;
import com.awbd.online_learning.dtos.UserDTO;
import com.awbd.online_learning.exceptions.ResourceExistsException;
import com.awbd.online_learning.repository.AuthorityRepository;
import com.awbd.online_learning.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest_Unit {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_whenUsernameIsNew_shouldSaveUser() {
        // Given
        UserDTO userDTO = new UserDTO("newUser", "password123");
        Authority defaultRole = Authority.builder().id(1L).name("ROLE_STUDENT").build();

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(authorityRepository.findByName("ROLE_STUDENT")).thenReturn(Optional.of(defaultRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        userService.registerUser(userDTO);

        // Then
        // capture  User object passed to the save method to inspect it
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("newUser");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getAuthorities()).contains(defaultRole);
        assertThat(savedUser.isEnabled()).isTrue();
    }

    @Test
    void registerUser_whenUsernameExists_shouldThrowResourceExistsException() {
        // Given
        UserDTO userDTO = new UserDTO("existingUser", "password123");
        User existingUser = User.builder().username("existingUser").build();
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingUser));

        // When / Then
        assertThrows(ResourceExistsException.class, () -> userService.registerUser(userDTO));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
