package com.example.goodreads.service.user;

import com.example.goodreads.dto.user.export.BookExportDto;
import com.example.goodreads.dto.user.export.ReviewExportDto;
import com.example.goodreads.dto.user.export.ShelfExportDto;
import com.example.goodreads.dto.user.export.UserExportDto;
import com.example.goodreads.exception.UsernameNotFoundException;
import com.example.goodreads.model.*;
import com.example.goodreads.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserExportServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserExportService userExportService;

    private User user;
    private Author author;
    private Book book;
    private Shelf shelf;
    private ShelfBook shelfBook;
    private Review review;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        author = new Author("Test Author");
        author.setId(1L);

        book = new Book("Test Book", author, "1234567890", 2023);
        book.setId(1L);

        shelf = new Shelf("Read");
        shelf.setId(1L);
        shelf.setType(ShelfType.READ);
        shelf.setUser(user);

        shelfBook = new ShelfBook();
        shelfBook.setShelf(shelf);
        shelfBook.setBook(book);
        shelfBook.setDateAdded(LocalDateTime.now());

        shelf.setShelfBooks(Set.of(shelfBook));
        user.setShelves(Set.of(shelf));

        review = new Review(5, "Great book!", user, book);
        review.setId(1L);
        review.setCreatedAt(LocalDateTime.now());

        user.setReviews(Set.of(review));
    }

    // --------------------------------------------------
    // exportAsJson
    // --------------------------------------------------

    @Test
    void testExportAsJsonSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserExportDto export = userExportService.exportAsJson("testuser");

        assertNotNull(export);
        assertEquals("testuser", export.getUsername());
        assertEquals("test@example.com", export.getEmail());
        assertNotNull(export.getExportedAt());

        assertEquals(1, export.getShelves().size());
        ShelfExportDto shelfDto = export.getShelves().get(0);
        assertEquals("Read", shelfDto.getName());
        assertEquals("READ", shelfDto.getType());
        assertEquals(1, shelfDto.getBooks().size());

        BookExportDto bookDto = shelfDto.getBooks().get(0);
        assertEquals("Test Book", bookDto.getTitle());
        assertEquals("Test Author", bookDto.getAuthor());
        assertEquals("1234567890", bookDto.getIsbn());
        assertEquals(2023, bookDto.getPublicationYear());

        assertEquals(1, export.getReviews().size());
        ReviewExportDto reviewDto = export.getReviews().get(0);
        assertEquals("Test Book", reviewDto.getBookTitle());
        assertEquals("Test Author", reviewDto.getBookAuthor());
        assertEquals(5, reviewDto.getRating());
        assertEquals("Great book!", reviewDto.getComment());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testExportAsJsonUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userExportService.exportAsJson("unknown"));

        verify(userRepository).findByUsername("unknown");
    }

    // --------------------------------------------------
    // exportAsCsv
    // --------------------------------------------------

    @Test
    void testExportAsCsvSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        String csv = userExportService.exportAsCsv("testuser");

        assertNotNull(csv);
        assertTrue(csv.contains("# SHELVES AND BOOKS"));
        assertTrue(csv.contains("shelf_name,shelf_type,book_title,book_author,book_isbn,book_year,date_added"));
        assertTrue(csv.contains("Read"));
        assertTrue(csv.contains("READ"));
        assertTrue(csv.contains("Test Book"));
        assertTrue(csv.contains("Test Author"));
        assertTrue(csv.contains("1234567890"));
        assertTrue(csv.contains("2023"));

        assertTrue(csv.contains("# REVIEWS"));
        assertTrue(csv.contains("book_title,book_author,rating,comment,created_at"));
        assertTrue(csv.contains("Great book!"));
        assertTrue(csv.contains("5"));

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testExportAsCsvUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userExportService.exportAsCsv("unknown"));

        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void testExportAsCsvWithSpecialCharacters() {
        Review reviewWithComma = new Review(4, "Good, but could be better", user, book);
        reviewWithComma.setId(2L);
        reviewWithComma.setCreatedAt(LocalDateTime.now());
        user.setReviews(Set.of(reviewWithComma));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        String csv = userExportService.exportAsCsv("testuser");

        assertNotNull(csv);
        // Comment with comma should be properly escaped in quotes
        assertTrue(csv.contains("\"Good, but could be better\""));

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testExportAsJsonWithEmptyShelves() {
        user.setShelves(new HashSet<>());
        user.setReviews(new HashSet<>());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserExportDto export = userExportService.exportAsJson("testuser");

        assertNotNull(export);
        assertEquals("testuser", export.getUsername());
        assertTrue(export.getShelves().isEmpty());
        assertTrue(export.getReviews().isEmpty());

        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testExportAsCsvWithEmptyData() {
        user.setShelves(new HashSet<>());
        user.setReviews(new HashSet<>());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        String csv = userExportService.exportAsCsv("testuser");

        assertNotNull(csv);
        assertTrue(csv.contains("# SHELVES AND BOOKS"));
        assertTrue(csv.contains("# REVIEWS"));

        verify(userRepository).findByUsername("testuser");
    }
}
