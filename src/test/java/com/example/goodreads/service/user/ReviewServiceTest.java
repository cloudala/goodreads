package com.example.goodreads.service.user;

import com.example.goodreads.dto.review.ReviewRequest;
import com.example.goodreads.dto.review.ReviewResponse;
import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Review;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Book book;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        reviewRequest = new ReviewRequest();
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great book!");
    }

    @Test
    void testAddReview() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        Review savedReview = new Review(5, "Great book!", user, book);
        savedReview.setId(1L);

        when(reviewRepository.save(Mockito.any())).thenReturn(savedReview);

        ReviewResponse reviewResponse = reviewService.addReview("testuser", 1L, reviewRequest);

        assertNotNull(reviewResponse);
        assertEquals(5, reviewResponse.getRating());
        assertEquals("Great book!", reviewResponse.getComment());
        assertEquals("testuser", reviewResponse.getUsername());

        verify(reviewRepository, Mockito.times(1)).save(Mockito.any(Review.class));
    }

    @Test
    void testAddReviewBookNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            reviewService.addReview("testuser", 1L, reviewRequest);
        });

        verify(reviewRepository, Mockito.never()).save(Mockito.any(Review.class));
    }

    @Test
    void testAddReviewReviewAlreadyExists() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(reviewRepository.findByBookIdAndUserId(1L, 1L)).thenReturn(Optional.of(new Review()));

        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.addReview("testuser", 1L, reviewRequest);
        });

        verify(reviewRepository, Mockito.never()).save(Mockito.any(Review.class));
    }

    @Test
    void testGetBookReviews() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        Review review = new Review(5, "Great book!", user, book);
        review.setId(1L);
        review.setCreatedAt(LocalDateTime.now());

        when(reviewRepository.findByBookId(1L)).thenReturn(List.of(review));

        List<ReviewResponse> reviews = reviewService.getBookReviews(1L);

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
        assertEquals(5, reviews.get(0).getRating());
        assertEquals("Great book!", reviews.get(0).getComment());
        assertEquals("testuser", reviews.get(0).getUsername());

        verify(reviewRepository, Mockito.times(1)).findByBookId(1L);
    }
}