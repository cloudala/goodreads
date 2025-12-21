package com.example.goodreads.service.admin;

import com.example.goodreads.dto.admin.review.AdminReviewResponse;
import com.example.goodreads.exception.ReviewNotFoundException;
import com.example.goodreads.model.Review;
import com.example.goodreads.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminReviewService {
    private ReviewRepository reviewRepository;

    public AdminReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    // --------- MAPPER METHOD ---------
    private AdminReviewResponse mapToResponse(Review review) {
        return new AdminReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getUser().getUsername(),
                review.getBook().getTitle(),
                review.getCreatedAt()
        );
    }
    // --------- CRUD METHODS --------
    public List<AdminReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream().map(review -> mapToResponse(review)).toList();
    }

    public AdminReviewResponse getReviewById(Long id) {
        Review review = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));
        return mapToResponse(review);
    }

    public void deleteReview(Long id) {
        Review review = reviewRepository
                .findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review with id " + id + " not found"));
        reviewRepository.delete(review);
    }
}
