package com.example.goodreads.controller.rest.user;

import com.example.goodreads.dto.user.UpdateUserRequest;
import com.example.goodreads.dto.user.UpdateUserResponse;
import com.example.goodreads.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/update")
    public ResponseEntity<UpdateUserResponse> updateUser(Authentication authentication, @RequestBody UpdateUserRequest updateUserRequest) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(userService.updateOwnProfile(currentUsername, updateUserRequest));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        userService.deleteOwnAccount(currentUsername);
        return ResponseEntity.ok("User account deleted successfully");
    }
}
