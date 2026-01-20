package com.example.goodreads.service;

import com.example.goodreads.model.Role;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(
                userDetails.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))
        );

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLoadUserByUsernameUserNotFound() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown")
        );

        verify(userRepository).findByUsername("unknown");
    }
}
