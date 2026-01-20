package com.example.goodreads.service.admin;

import com.example.goodreads.dto.admin.user.AdminCreateUserRequest;
import com.example.goodreads.dto.admin.user.AdminUpdateUserRequest;
import com.example.goodreads.dto.admin.user.AdminUserResponse;
import com.example.goodreads.exception.UsernameAlreadyExistsException;
import com.example.goodreads.exception.UsernameNotFoundException;
import com.example.goodreads.model.Role;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserService adminUserService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.USER);
        user.setPassword("encodedpassword");
    }

    // ---------- GET ALL USERS ----------
    @Test
    void testReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<AdminUserResponse> users = adminUserService.getAllUsers();

        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUsername()).isEqualTo("testuser");
        assertThat(users.get(0).getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findAll();
    }

    // ---------- GET USER BY ID ----------
    @Test
    void testReturnUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AdminUserResponse response = adminUserService.getUserById(1L);

        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(1L);
    }

    @Test
    void testThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.getUserById(99L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User with id 99 not found");

        verify(userRepository).findById(99L);
    }

    // ---------- CREATE USER ----------
    @Test
    void testCreateUserSuccessfully() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password");
        request.setRole(Role.USER);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AdminUserResponse response = adminUserService.createUser(request);

        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("new@example.com");

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void testThrowExceptionWhenUsernameExists() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> adminUserService.createUser(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username testuser already exists");

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
    }

    // ---------- UPDATE USER ----------
    @Test
    void testUpdateUserSuccessfully() {
        AdminUpdateUserRequest request = new AdminUpdateUserRequest();
        request.setUsername("updateduser");
        request.setEmail("updated@example.com");
        request.setPassword("newpass");
        request.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodednewpass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AdminUserResponse response = adminUserService.updateUser(1L, request);

        assertThat(response.getUsername()).isEqualTo("updateduser");
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
        assertThat(response.getRole()).isEqualTo(Role.ADMIN);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("newpass");
    }

    // ---------- DELETE USER ----------
    @Test
    void testDeleteUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUserService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
