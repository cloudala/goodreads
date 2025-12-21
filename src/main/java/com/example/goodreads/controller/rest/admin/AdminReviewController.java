package com.example.goodreads.controller.rest.admin;

import com.example.goodreads.dto.admin.review.AdminReviewResponse;
import com.example.goodreads.service.admin.AdminReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reviews")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {
    private final AdminReviewService adminReviewService;

    public AdminReviewController(AdminReviewService adminReviewService) {
        this.adminReviewService = adminReviewService;
    }

    @GetMapping
    public ResponseEntity<List<AdminReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(adminReviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminReviewResponse> getReviewById(@PathVariable Long id){
        return ResponseEntity.ok(adminReviewService.getReviewById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        adminReviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
