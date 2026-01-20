package com.example.goodreads.service.user;

import com.example.goodreads.dto.PaginatedResponse;
import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.dto.book.BookStatsResponse;
import com.example.goodreads.dto.book.BookWithReviewsResponse;
import com.example.goodreads.dto.book.PopularBookResponse;
import com.example.goodreads.dto.review.ReviewResponse;
import com.example.goodreads.model.Author;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Review;
import com.example.goodreads.repository.AuthorRepository;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final ReviewRepository reviewRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.reviewRepository = reviewRepository;
    }

    private Author getOrCreateAuthor(String name) {
        return authorRepository
                .findByNameIgnoreCase(name)
                .orElseGet(() -> authorRepository.save(new Author(name)));
    }


    // Return a few featured books (hardcoded)
    public List<Book> getFeaturedBooks() {
        Author fitzgerald = getOrCreateAuthor("F. Scott Fitzgerald");
        Author austen = getOrCreateAuthor("Jane Austen");
        Author orwell = getOrCreateAuthor("George Orwell");
        return List.of(
                new Book("The Great Gatsby", fitzgerald, "9780743273565", 1925),
                new Book("Pride and Prejudice", austen, "9780141439518", 1813),
                new Book("1984", orwell, "9780451524935", 1949)
        );
    }

    // Return a few recently added books (hardcoded)
    public List<Book> getRecentBooks() {
        Author haig = getOrCreateAuthor("Matt Haig");
        Author ishiguro = getOrCreateAuthor("Kazuo Ishiguro");
        Author weir = getOrCreateAuthor("Andy Weir");
        Author herbert = getOrCreateAuthor("Frank Herbert");
        return List.of(
                new Book("The Midnight Library", haig, "9780525559474", 2020),
                new Book("Klara and the Sun", ishiguro, "9780593318171", 2021),
                new Book("Project Hail Mary", weir, "9780593135204", 2021),
                new Book("Dune", herbert, "9780441013593", 1965)
        );
    }

    // ---------------- Private helper method ----------------
    private List<BookResponse> mapBooksWithAvgRating(List<Object[]> results) {
        return results.stream()
                .map(obj -> {
                    Book book = (Book) obj[0];
                    Double avgRating = (Double) obj[1];
                    return new BookResponse(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor().getName(),
                            // book.getAuthor(),
                            avgRating
                    );
                })
                .collect(Collectors.toList());
    }

    public PaginatedResponse<BookResponse> getAllBooks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = bookRepository.findAllBooksWithAverageRating(pageable);
        System.out.println(results.getContent().get(0)[0].toString());
        List<BookResponse> content = mapBooksWithAvgRating(results.getContent());
        return new PaginatedResponse<>(content, results.getNumber(), results.getSize(), results.getTotalElements(), results.getTotalPages());
    }

    public BookWithReviewsResponse getBookById(Long bookId) {
        List<Object[]> resultList = bookRepository.findBookByIdWithAverageRating(bookId);
        Object[] result = resultList.stream().findFirst().orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        if (result == null || result[0] == null) {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
        Book book = (Book) result[0];
        Double avgRating = (Double) result[1];
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        List<ReviewResponse> reviewResponses = reviews.stream()
                .map(r -> new ReviewResponse(r.getId(), r.getRating(), r.getComment(), r.getUser().getUsername(), r.getCreatedAt()))
                .collect(Collectors.toList());
        return new BookWithReviewsResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getName(),
                avgRating,
                reviewResponses
        );
    }

    public PaginatedResponse<BookResponse> searchBooks(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = bookRepository.searchBooksWithAverageRating(query, pageable);
        List<BookResponse> content = mapBooksWithAvgRating(results.getContent());
        return new PaginatedResponse<>(content, results.getNumber(), results.getSize(), results.getTotalElements(), results.getTotalPages());
    }

    public BookStatsResponse getBookStats(Long bookId) {
        Object[] bookAndAvgRating = bookRepository.findBookByIdWithAverageRating(bookId)
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        Double averageRating = (Double) bookAndAvgRating[1];
        Long readerCount = bookRepository.countTotalReadersByBookId(bookId);
        Map<Long, Long> reviewDistribution = getReviewDistribution(bookId);
        return new BookStatsResponse(bookId, readerCount, reviewDistribution, averageRating);
    }

    public Map<Long, Long> getReviewDistribution(Long bookId) {
        List<Long[]> results = reviewRepository.getReviewDistributionByBookId(bookId);

        // Initialize with all ratings (1-5) defaulting to 0
        Map<Long, Long> distribution = new HashMap<>();
        for (long i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }

        // Populate from query results
        for (Long[] row : results) {
            Long rating = (Long) row[0];
            Long count = (Long) row[1];
            distribution.put(rating, count);
        }

        return distribution;
    }

    public List<PopularBookResponse> getPopularBooks() {
        List<PopularBookResponse> popularBooks = bookRepository.findTop10BooksByMostReadsAfterDate(LocalDateTime.now().minusMonths(1));
        return popularBooks;
    }
}
