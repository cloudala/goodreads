package com.example.goodreads.controller.rest.user;

import com.example.goodreads.dto.user.UpdateUserRequest;
import com.example.goodreads.dto.user.UpdateUserResponse;
import com.example.goodreads.dto.user.export.UserExportDto;
import com.example.goodreads.service.user.UserExportService;
import com.example.goodreads.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class UserController {
    private final UserService userService;
    private final UserExportService userExportService;

    public UserController(UserService userService, UserExportService userExportService) {
        this.userService = userService;
        this.userExportService = userExportService;
    }

    @PreAuthorize("authentication.principal.isAccountNonLocked()")
    @PutMapping("/update")
    public ResponseEntity<UpdateUserResponse> updateUser(Authentication authentication,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        String currentUsername = authentication.getName();
        return ResponseEntity.ok(userService.updateOwnProfile(currentUsername, updateUserRequest));
    }

    @PreAuthorize("authentication.principal.isAccountNonLocked()")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(Authentication authentication) {
        String currentUsername = authentication.getName();
        userService.deleteOwnAccount(currentUsername);
        return ResponseEntity.ok("User account deleted successfully");
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportUserData(
            Authentication authentication,
            @RequestParam(defaultValue = "json") String format) {
        String username = authentication.getName();

        if ("csv".equalsIgnoreCase(format)) {
            String csv = userExportService.exportAsCsv(username);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_export.csv\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csv);
        } else {
            UserExportDto export = userExportService.exportAsJson(username);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"user_export.json\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(export);
        }
    }
}
