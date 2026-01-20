package com.example.goodreads.repository;

import com.example.goodreads.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;
    private Book book;
    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setName("Test Author");
        testEntityManager.persist(author);

        book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setIsbn("123456789");
        book.setPublicationYear(2018);
        testEntityManager.persist(book);

        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@gmail.com");
        user.setPassword("12345");
        user.setRole(Role.USER);
        testEntityManager.persist(user);

        testEntityManager.flush();
    }

    @Test
    void testFindByBookId() {
        Review review = new Review(5, "Great book!", user, book);
        testEntityManager.persist(review);
        testEntityManager.flush();

        List<Review> foundReviews = reviewRepository.findByBookId(book.getId());
        assertEquals(1, foundReviews.size());
        assertEquals("Great book!", foundReviews.get(0).getComment());
    }

    @Test
    void testFindByBookIdAndUserId() {
        Review review = new Review(4, "Good read", user, book);
        testEntityManager.persist(review);
        testEntityManager.flush();

        Optional<Review> foundReview = reviewRepository.findByBookIdAndUserId(book.getId(), user.getId());
        assertTrue(foundReview.isPresent());
        assertEquals("Good read", foundReview.get().getComment());
        assertThat(foundReview.get().getUser().getUsername()).isEqualTo("testuser");
    }
}