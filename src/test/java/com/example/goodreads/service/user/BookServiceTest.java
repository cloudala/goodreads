package com.example.goodreads.service.user;

import com.example.goodreads.dto.PaginatedResponse;
import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.dto.book.BookWithReviewsResponse;
import com.example.goodreads.dto.review.ReviewResponse;
import com.example.goodreads.model.Author;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Review;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.AuthorRepository;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private BookService bookService;

    private Author author;
    private Book book;
    private User user;
    private Review review;

    @BeforeEach
    void setUp() {
        author = new Author("Test Author");
        author.setId(1L);

        book = new Book("Test Book", author, "1234567890", 2023);
        book.setId(1L);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        review = new Review(5, "Excellent", user, book);
        review.setId(1L);
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetFeaturedBooks() {
        when(authorRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(author));

        List<Book> featured = bookService.getFeaturedBooks();

        assertEquals(3, featured.size());
        assertTrue(featured.stream().anyMatch(b -> b.getTitle().equals("The Great Gatsby")));

        verify(authorRepository, times(3)).findByNameIgnoreCase(anyString());
    }

    @Test
    void testGetRecentBooks() {
        when(authorRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(author));

        List<Book> recent = bookService.getRecentBooks();

        assertEquals(4, recent.size());
        assertTrue(recent.stream().anyMatch(b -> b.getTitle().equals("The Midnight Library")));

        verify(authorRepository, times(4)).findByNameIgnoreCase(anyString());
    }

    @Test
    void testGetBookByIdSuccess() {
        Object[] bookWithRating = new Object[]{book, 4.5};

        List<Object[]> bookList = List.<Object[]>of(bookWithRating);

        when(bookRepository.findBookByIdWithAverageRating(1L)).thenReturn(bookList);
        when(reviewRepository.findByBookId(1L)).thenReturn(List.of(review));

        BookWithReviewsResponse response = bookService.getBookById(1L);

        assertNotNull(response);
        assertEquals("Test Book", response.getTitle());
        assertEquals("Test Author", response.getAuthor());
        assertEquals(1, response.getReviews().size());
        assertEquals("Excellent", response.getReviews().get(0).getComment());

        verify(bookRepository, times(1)).findBookByIdWithAverageRating(1L);
        verify(reviewRepository, times(1)).findByBookId(1L);
    }


    @Test
    void testGetBookByIdNotFound() {
        when(bookRepository.findBookByIdWithAverageRating(2L)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> bookService.getBookById(2L));

        verify(bookRepository, times(1)).findBookByIdWithAverageRating(2L);
        verify(reviewRepository, never()).findByBookId(anyLong());
    }

    @Test
    void testGetAllBooks() {
        Object[] obj = new Object[]{book, 4.0};

        List<Object[]> listObj = List.<Object[]>of(obj);

        Page<Object[]> page = new PageImpl<>(listObj);

        when(bookRepository.findAllBooksWithAverageRating(any(Pageable.class)))
                .thenReturn(page);

        PaginatedResponse<BookResponse> response = bookService.getAllBooks(0, 10);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Test Book", response.getContent().get(0).getTitle());
        assertEquals(4.0, response.getContent().get(0).getAverageRating());

        verify(bookRepository, times(1)).findAllBooksWithAverageRating(any(Pageable.class));
    }



    @Test
    void testSearchBooks() {
        Object[] obj = new Object[]{book, 4.0};
        List<Object[]> list = List.<Object[]>of(obj);
        Page<Object[]> page = new PageImpl<>(list);

        when(bookRepository.searchBooksWithAverageRating(eq("Test"), any(Pageable.class)))
                .thenReturn(page);

        PaginatedResponse<BookResponse> response = bookService.searchBooks("Test", 0, 10);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Test Book", response.getContent().get(0).getTitle());
        assertEquals(4.0, response.getContent().get(0).getAverageRating());

        verify(bookRepository, times(1)).searchBooksWithAverageRating(eq("Test"), any(Pageable.class));
    }

}
