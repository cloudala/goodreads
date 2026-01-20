package com.example.goodreads.controller.rest.user;

import com.example.goodreads.dto.book.BookStatsResponse;
import com.example.goodreads.dto.user.UserReadingStatsResponse;
import com.example.goodreads.service.user.BookService;
import com.example.goodreads.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("isAuthenticated()")
public class StatsController {

    private final UserService userService;
    private final BookService bookService;

    public StatsController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    @GetMapping("/user")
    public ResponseEntity<UserReadingStatsResponse> getUserReadingStats(Authentication authentication) {
        String username = authentication.getName();
        UserReadingStatsResponse userReadingStatsResponse = userService.getUserReadingStats(username);
        return ResponseEntity.ok(userReadingStatsResponse);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<BookStatsResponse> getBookStats(@PathVariable Long bookId) {
        BookStatsResponse bookStatsResponse = bookService.getBookStats(bookId);
        return ResponseEntity.ok(bookStatsResponse);
    }
}
