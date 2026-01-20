package com.example.goodreads.repository;

import com.example.goodreads.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setName("Test Author");
        testEntityManager.persist(author);

        book = new Book();
        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setIsbn("123456789");
        book.setPublicationYear(2020);
        testEntityManager.persist(book);

        User user = new User();
        user.setUsername("reviewer");
        user.setEmail("reviewer@test.com");
        user.setPassword("pass");
        user.setRole(Role.USER);
        testEntityManager.persist(user);

        Review review = new Review(5, "Great book!", user, book);
        testEntityManager.persist(review);

        testEntityManager.flush();
    }

    @Test
    void testFindAllBooksWithAverageRating() {
        Page<Object[]> page = bookRepository.findAllBooksWithAverageRating(PageRequest.of(0, 10));
        assertEquals(1, page.getContent().size());
        Object[] result = page.getContent().get(0);
        Book b = (Book) result[0];
        Double avgRating = (Double) result[1];
        assertEquals("Test Book", b.getTitle());
        assertEquals(5.0, avgRating);
    }

    @Test
    void testSearchBooksWithAverageRating() {
        Page<Object[]> page = bookRepository.searchBooksWithAverageRating("test", PageRequest.of(0, 10));
        assertEquals(1, page.getContent().size());
        Object[] result = page.getContent().get(0);
        Book b = (Book) result[0];
        Double avgRating = (Double) result[1];
        assertEquals("Test Book", b.getTitle());
        assertEquals(5.0, avgRating);
    }

    @Test
    void testFindBookByIdWithAverageRating() {
        List<Object[]> results = bookRepository.findBookByIdWithAverageRating(book.getId());
        assertEquals(1, results.size());
        Object[] result = results.get(0);
        Book b = (Book) result[0];
        Double avgRating = (Double) result[1];
        assertEquals("Test Book", b.getTitle());
        assertEquals(5.0, avgRating);
    }

    @Test
    void testSearchByTitleOrAuthorOrIsbn() {
        List<Book> books = bookRepository.searchByTitleOrAuthorOrIsbn("123456789");
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
    }

    @Test
    void testFindAllWithAuthor() {
        List<Book> books = bookRepository.findAllWithAuthor();
        assertEquals(1, books.size());
        assertEquals("Test Author", books.get(0).getAuthor().getName());
    }

    @Test
    void testFindByTitleIgnoreCaseWithAuthor_Found() {
        Optional<Book> foundBook = bookRepository.findByTitleIgnoreCaseWithAuthor("test book");
        assertTrue(foundBook.isPresent());
        assertEquals("Test Book", foundBook.get().getTitle());
        assertEquals("Test Author", foundBook.get().getAuthor().getName());
    }

    @Test
    void testFindByTitleIgnoreCaseWithAuthor_NotFound() {
        Optional<Book> foundBook = bookRepository.findByTitleIgnoreCaseWithAuthor("Nonexistent Book");
        assertFalse(foundBook.isPresent());
    }

    @Test
    void testFindById() {
        Optional<Book> foundBook = bookRepository.findById(book.getId());
        assertTrue(foundBook.isPresent());
        assertEquals("Test Book", foundBook.get().getTitle());
    }
}
