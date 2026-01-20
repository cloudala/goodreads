package com.example.goodreads.controller.rest;

import com.example.goodreads.dto.auth.LoginRequest;
import com.example.goodreads.dto.auth.LoginResponse;
import com.example.goodreads.dto.auth.RegisterRequest;
import com.example.goodreads.dto.auth.RegisterResponse;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.UserRepository;
import com.example.goodreads.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(UserRepository userRepository,
                          AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @GetMapping("/me")
    public User getCurrentUser(Authentication authentication) {
        System.out.println("Getting current user with authentication: " + authentication);
        return authService.getCurrentUser(authentication.getName());
    }
}
