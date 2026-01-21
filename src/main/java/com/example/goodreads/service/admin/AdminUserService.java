package com.example.goodreads.service.admin;

import com.example.goodreads.dto.admin.user.AdminCreateUserRequest;
import com.example.goodreads.dto.admin.user.AdminUpdateUserRequest;
import com.example.goodreads.dto.admin.user.AdminUserResponse;
import com.example.goodreads.exception.UsernameAlreadyExistsException;
import com.example.goodreads.exception.UsernameNotFoundException;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // --------- PRIVATE HELPER METHOD ---------
    public User getUserByIdInternal(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
    }

    // --------- MAPPER METHOD ---------
    private AdminUserResponse mapToResponse(User user) {
        return new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isLocked()
        );
    }

    // --------- CRUD METHODS ---------
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> mapToResponse(user)).toList();
    }

    public AdminUserResponse getUserById(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id " + id + " not found"));
        return mapToResponse(user);
    }

    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException("Username " + request.getUsername() + " already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        userRepository.save(user);
        return mapToResponse(user);
    }

    public AdminUserResponse updateUser(Long id, AdminUpdateUserRequest request) {
        User user = getUserByIdInternal(id);

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        userRepository.save(user);
        return mapToResponse(user);
    }


    public void deleteUser(Long id) {
        User user = getUserByIdInternal(id);
        userRepository.delete(user);
    }

    public AdminUserResponse lockUserAccount(Long id) {
        User user = getUserByIdInternal(id);
        user.setLocked(true);
        userRepository.save(user);
        return mapToResponse(user);
    }

    public AdminUserResponse unlockUserAccount(Long id) {
        User user = getUserByIdInternal(id);
        user.setLocked(false);
        userRepository.save(user);
        return mapToResponse(user);
    }
}
