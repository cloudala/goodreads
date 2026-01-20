package com.example.goodreads.controller.rest.user;

import com.example.goodreads.dto.review.ReviewRequest;
import com.example.goodreads.dto.review.ReviewResponse;
import com.example.goodreads.service.user.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getBookReviews(@PathVariable Long bookId) {
        List<ReviewResponse> reviews = reviewService.getBookReviews(bookId);
        return ResponseEntity.ok(reviews);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(Authentication authentication,
            @PathVariable Long bookId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        String currentUsername = authentication.getName();
        ReviewResponse reviewResponse = reviewService.addReview(currentUsername, bookId, reviewRequest);
        return ResponseEntity.ok(reviewResponse);
    }
}
