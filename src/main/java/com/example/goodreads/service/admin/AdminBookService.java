package com.example.goodreads.service.admin;

import com.example.goodreads.dto.admin.book.AdminBookResponse;
import com.example.goodreads.dto.admin.book.AdminCreateBookRequest;
import com.example.goodreads.dto.admin.book.AdminUpdateBookRequest;
import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Shelf;
import com.example.goodreads.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminBookService {
    private final BookRepository bookRepository;

    public AdminBookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // --------- PRIVATE HELPER METHOD ---------
    public Book getBookByIdInternal(Long id) {
        return bookRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    }

    // --------- MAPPER METHOD ---------
    private AdminBookResponse mapToResponse(Book book) {
        return new AdminBookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublicationYear()
        );
    }

    // --------- CRUD METHODS --------
    public List<AdminBookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(book -> mapToResponse(book)).toList();
    }

    public AdminBookResponse getBookById(Long id) {
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
        return mapToResponse(book);
    }

    public AdminBookResponse createBook(AdminCreateBookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
        Book savedBook = bookRepository.save(book);
        return mapToResponse(savedBook);
    }

    public AdminBookResponse updateBook(Long id, AdminUpdateBookRequest request) {
        Book book = getBookByIdInternal(id);
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getIsbn() != null) {
            book.setIsbn(request.getIsbn());
        }
        if (request.getPublicationYear() != null) {
            book.setPublicationYear(request.getPublicationYear());
        }
        Book updatedBook = bookRepository.save(book);
        return mapToResponse(updatedBook);
    }

    public void deleteBook(Long id) {
        Book book = getBookByIdInternal(id);

        // Remove book from all shelves before deletion
        for (Shelf shelf : new HashSet<>(book.getShelves())) {
            shelf.getBooks().remove(book);
            book.getShelves().remove(shelf);
        }

        bookRepository.delete(book);
    }
}
