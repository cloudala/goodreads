package com.example.goodreads.controller.rest.user;

import com.example.goodreads.dto.PaginatedResponse;
import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.service.user.BookService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponse<BookResponse>> searchBooks(@RequestParam String query,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<BookResponse> results = bookService.searchBooks(query, page, size);
        return ResponseEntity.ok(results);
    }

}
