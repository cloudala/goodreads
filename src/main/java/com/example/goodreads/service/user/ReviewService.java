package com.example.goodreads.service.user;

import com.example.goodreads.dto.review.ReviewRequest;
import com.example.goodreads.dto.review.ReviewResponse;
import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Review;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public ReviewService(ReviewRepository reviewRepository, BookRepository bookRepository, UserService userService) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    @Transactional
    public ReviewResponse addReview(String username, Long bookId, ReviewRequest reviewRequest) {
        User user = userService.getCurrentUser(username);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + bookId + " not found"));

        reviewRepository.findByBookIdAndUserId(bookId, user.getId())
                        .ifPresent(existingReview -> {;
                            throw new IllegalArgumentException("User has already reviewed this book");
                        });
        Review review = new Review(reviewRequest.getRating(), reviewRequest.getComment(), user, book);
        Review savedReview = reviewRepository.save(review);
        return new ReviewResponse(savedReview.getId(), savedReview.getRating(), savedReview.getComment(), savedReview.getUser().getUsername(), savedReview.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getBookReviews(Long bookId) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        return reviewRepository.findByBookId(bookId).stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getRating(),
                        r.getComment(),
                        r.getUser().getUsername(),
                        r.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
