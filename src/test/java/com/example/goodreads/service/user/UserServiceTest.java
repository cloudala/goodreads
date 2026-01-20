package com.example.goodreads.service.user;

import com.example.goodreads.dto.user.UpdateUserRequest;
import com.example.goodreads.dto.user.UpdateUserResponse;
import com.example.goodreads.exception.UsernameNotFoundException;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password");

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUsername("updatedUser");
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setPassword("newpassword");
    }

    @Test
    void testGetCurrentUserSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = userService.getCurrentUser("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetCurrentUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUser("unknown"));

        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void testUpdateOwnProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UpdateUserResponse response = userService.updateOwnProfile("testuser", updateUserRequest);

        assertNotNull(response);
        assertEquals("updatedUser", response.getUsername());
        assertEquals("updated@example.com", response.getEmail());
        assertEquals("encodedPassword", user.getPassword());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).encode("newpassword");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateOwnProfilePartialUpdate() {
        UpdateUserRequest partialUpdate = new UpdateUserRequest();
        partialUpdate.setEmail("partial@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UpdateUserResponse response = userService.updateOwnProfile("testuser", partialUpdate);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername()); // username unchanged
        assertEquals("partial@example.com", response.getEmail()); // email updated

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testDeleteOwnAccount() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        userService.deleteOwnAccount("testuser");

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).delete(user);
    }
}
