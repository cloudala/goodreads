package com.example.goodreads.service.user;

import com.example.goodreads.dto.PaginatedResponse;
import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.dto.book.BookStatsResponse;
import com.example.goodreads.dto.book.BookWithReviewsResponse;
import com.example.goodreads.dto.book.PopularBookResponse;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
        Object[] bookWithRating = new Object[] { book, 4.5 };

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
        Object[] obj = new Object[] { book, 4.0 };

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
        Object[] obj = new Object[] { book, 4.0 };
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

    @Test
    void testGetBookStats() {
        Object[] bookWithRating = new Object[] { book, 4.5 };
        List<Object[]> bookList = List.<Object[]>of(bookWithRating);

        when(bookRepository.findBookByIdWithAverageRating(1L)).thenReturn(bookList);
        when(bookRepository.countTotalReadersByBookId(1L)).thenReturn(100L);

        Long[] rating5 = new Long[] { 5L, 10L };
        Long[] rating4 = new Long[] { 4L, 5L };
        List<Long[]> distribution = List.of(rating5, rating4);
        when(reviewRepository.getReviewDistributionByBookId(1L)).thenReturn(distribution);

        BookStatsResponse response = bookService.getBookStats(1L);

        assertNotNull(response);
        assertEquals(1L, response.getBookId());
        assertEquals(100L, response.getTotalReaders());
        assertEquals(4.5, response.getAverageRating());
        assertEquals(10L, response.getRatingsDistribution().get(5L));
        assertEquals(5L, response.getRatingsDistribution().get(4L));
        assertEquals(0L, response.getRatingsDistribution().get(3L));

        verify(bookRepository).findBookByIdWithAverageRating(1L);
        verify(bookRepository).countTotalReadersByBookId(1L);
        verify(reviewRepository).getReviewDistributionByBookId(1L);
    }

    @Test
    void testGetBookStatsBookNotFound() {
        when(bookRepository.findBookByIdWithAverageRating(2L)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> bookService.getBookStats(2L));

        verify(bookRepository).findBookByIdWithAverageRating(2L);
    }

    @Test
    void testGetReviewDistribution() {
        Long[] rating5 = new Long[] { 5L, 15L };
        Long[] rating3 = new Long[] { 3L, 8L };
        List<Long[]> results = List.of(rating5, rating3);

        when(reviewRepository.getReviewDistributionByBookId(1L)).thenReturn(results);

        Map<Long, Long> distribution = bookService.getReviewDistribution(1L);

        assertNotNull(distribution);
        assertEquals(5, distribution.size());
        assertEquals(15L, distribution.get(5L));
        assertEquals(0L, distribution.get(4L));
        assertEquals(8L, distribution.get(3L));
        assertEquals(0L, distribution.get(2L));
        assertEquals(0L, distribution.get(1L));

        verify(reviewRepository).getReviewDistributionByBookId(1L);
    }

    @Test
    void testGetPopularBooks() {
        PopularBookResponse popularBook = new PopularBookResponse(1L, "Test Book", "Test Author", 4.5, 50L);
        List<PopularBookResponse> popularBooks = List.of(popularBook);

        when(bookRepository.findTop10BooksByMostReadsAfterDate(any(LocalDateTime.class)))
                .thenReturn(popularBooks);

        List<PopularBookResponse> result = bookService.getPopularBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        assertEquals(50L, result.get(0).getReadersLastMonth());

        verify(bookRepository).findTop10BooksByMostReadsAfterDate(any(LocalDateTime.class));
    }

}
