package com.example.goodreads.controller.rest.user;

import com.example.goodreads.dto.PaginatedResponse;
import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.dto.book.BookWithReviewsResponse;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.service.user.BookService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<BookResponse>> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookWithReviewsResponse> getBookById(@PathVariable Long bookId) {
        BookWithReviewsResponse bookWithReviewsResponse = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookWithReviewsResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<BookResponse>> searchBooks(@RequestParam String query,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<BookResponse> results = bookService.searchBooks(query, page, size);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PopularBookResponse>> getPopularBooks() {
        List<PopularBookResponse> popularBooks = bookService.getPopularBooks();
        return ResponseEntity.ok(popularBooks);
    }
}
