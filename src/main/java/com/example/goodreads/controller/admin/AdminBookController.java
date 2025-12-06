package com.example.goodreads.controller.admin;

import com.example.goodreads.dto.admin.book.AdminBookResponse;
import com.example.goodreads.dto.admin.book.AdminCreateBookRequest;
import com.example.goodreads.dto.admin.book.AdminUpdateBookRequest;
import com.example.goodreads.service.admin.AdminBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/books")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookController {
    private final AdminBookService adminBookService;

    public AdminBookController(AdminBookService adminBookService) {
        this.adminBookService = adminBookService;
    }

    @GetMapping
    public ResponseEntity<List<AdminBookResponse>> getAllBooks() {
        return ResponseEntity.ok(adminBookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminBookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(adminBookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<AdminBookResponse> createBook(@RequestBody AdminCreateBookRequest request) {
        return ResponseEntity.ok(adminBookService.createBook(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminBookResponse> updateBook(@PathVariable Long id, @RequestBody AdminUpdateBookRequest request) {
        return ResponseEntity.ok(adminBookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        adminBookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
