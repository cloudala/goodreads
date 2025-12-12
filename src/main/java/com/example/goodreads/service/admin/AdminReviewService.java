package com.example.goodreads.service.admin;

import com.example.goodreads.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminReviewService {
    private ReviewRepository reviewRepository;

    public AdminReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
}
