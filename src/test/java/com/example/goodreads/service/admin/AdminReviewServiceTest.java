package com.example.goodreads.service.admin;

import com.example.goodreads.dto.admin.review.AdminReviewResponse;
import com.example.goodreads.exception.ReviewNotFoundException;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Review;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AdminReviewService adminReviewService;

    private Review review;
    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Great book!");
        review.setUser(user);
        review.setBook(book);
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllReviews() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));

        List<AdminReviewResponse> responses = adminReviewService.getAllReviews();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(5, responses.get(0).getRating());
        assertEquals("Great book!", responses.get(0).getComment());
        assertEquals("testuser", responses.get(0).getUsername());
        assertEquals("Test Book", responses.get(0).getBookTitle());

        verify(reviewRepository).findAll();
    }

    @Test
    void testGetReviewByIdSuccess() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        AdminReviewResponse response = adminReviewService.getReviewById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(5, response.getRating());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test Book", response.getBookTitle());

        verify(reviewRepository).findById(1L);
    }

    @Test
    void testGetReviewByIdNotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ReviewNotFoundException.class,
                () -> adminReviewService.getReviewById(1L)
        );

        verify(reviewRepository).findById(1L);
    }

    @Test
    void testDeleteReviewSuccess() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        adminReviewService.deleteReview(1L);

        verify(reviewRepository).findById(1L);
        verify(reviewRepository).delete(review);
    }

    @Test
    void testDeleteReviewNotFound() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ReviewNotFoundException.class,
                () -> adminReviewService.deleteReview(1L)
        );

        verify(reviewRepository).findById(1L);
    }
}
