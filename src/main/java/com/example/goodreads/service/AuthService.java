package com.example.goodreads.service;

import com.example.goodreads.dto.LoginRequest;
import com.example.goodreads.dto.LoginResponse;
import com.example.goodreads.dto.RegisterRequest;
import com.example.goodreads.dto.RegisterResponse;
import com.example.goodreads.exception.UsernameAlreadyExistsException;
import com.example.goodreads.model.Role;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.UserRepository;
import com.example.goodreads.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username " + registerRequest.getUsername() + " already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return new RegisterResponse(
                "User registered successfully",
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return new LoginResponse(jwt, userDetails.getUsername());
    }


    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
