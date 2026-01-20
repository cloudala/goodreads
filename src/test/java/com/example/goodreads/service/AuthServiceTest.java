package com.example.goodreads.service;

import com.example.goodreads.dto.auth.LoginRequest;
import com.example.goodreads.dto.auth.LoginResponse;
import com.example.goodreads.dto.auth.RegisterRequest;
import com.example.goodreads.dto.auth.RegisterResponse;
import com.example.goodreads.exception.UsernameAlreadyExistsException;
import com.example.goodreads.model.Role;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.UserRepository;
import com.example.goodreads.service.user.ShelfService;
import com.example.goodreads.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ShelfService shelfService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
    }

    // --------------------------------------------------
    // register
    // --------------------------------------------------

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());

        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder).encode("password");
        verify(userRepository, times(2)).save(any(User.class));
        verify(shelfService).createDefaultShelves(any(User.class));
    }

    @Test
    void testRegisterUsernameAlreadyExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () ->
                authService.register(registerRequest)
        );

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
        verify(shelfService, never()).createDefaultShelves(any());
    }

    // --------------------------------------------------
    // login
    // --------------------------------------------------

    @Test
    void testLoginSuccess() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("jwt-token");

        LoginResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("testuser");
    }

    // --------------------------------------------------
    // getCurrentUser
    // --------------------------------------------------

    @Test
    void testGetCurrentUserFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = authService.getCurrentUser("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testGetCurrentUserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        User result = authService.getCurrentUser("testuser");

        assertNull(result);

        verify(userRepository).findByUsername("testuser");
    }
}
