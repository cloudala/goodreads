package com.example.goodreads.service.user;

import com.example.goodreads.dto.user.export.*;
import com.example.goodreads.exception.UsernameNotFoundException;
import com.example.goodreads.model.*;
import com.example.goodreads.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserExportService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final UserRepository userRepository;

    public UserExportService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserExportDto exportAsJson(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<ShelfExportDto> shelves = user.getShelves().stream()
                .map(this::mapShelf)
                .collect(Collectors.toList());

        List<ReviewExportDto> reviews = user.getReviews().stream()
                .map(this::mapReview)
                .collect(Collectors.toList());

        return new UserExportDto(
                user.getUsername(),
                user.getEmail(),
                shelves,
                reviews,
                LocalDateTime.now().format(DATE_FORMATTER));
    }

    @Transactional(readOnly = true)
    public String exportAsCsv(String username) {
        UserExportDto export = exportAsJson(username);
        StringBuilder csv = new StringBuilder();

        // Header for shelves/books section
        csv.append("# SHELVES AND BOOKS\n");
        csv.append("shelf_name,shelf_type,book_title,book_author,book_isbn,book_year,date_added\n");

        for (ShelfExportDto shelf : export.getShelves()) {
            for (BookExportDto book : shelf.getBooks()) {
                csv.append(escapeCsv(shelf.getName())).append(",");
                csv.append(escapeCsv(shelf.getType())).append(",");
                csv.append(escapeCsv(book.getTitle())).append(",");
                csv.append(escapeCsv(book.getAuthor())).append(",");
                csv.append(escapeCsv(book.getIsbn())).append(",");
                csv.append(book.getPublicationYear()).append(",");
                csv.append(escapeCsv(book.getDateAdded())).append("\n");
            }
        }

        // Header for reviews section
        csv.append("\n# REVIEWS\n");
        csv.append("book_title,book_author,rating,comment,created_at\n");

        for (ReviewExportDto review : export.getReviews()) {
            csv.append(escapeCsv(review.getBookTitle())).append(",");
            csv.append(escapeCsv(review.getBookAuthor())).append(",");
            csv.append(review.getRating()).append(",");
            csv.append(escapeCsv(review.getComment())).append(",");
            csv.append(escapeCsv(review.getCreatedAt())).append("\n");
        }

        return csv.toString();
    }

    private ShelfExportDto mapShelf(Shelf shelf) {
        List<BookExportDto> books = shelf.getShelfBooks().stream()
                .map(this::mapShelfBook)
                .collect(Collectors.toList());

        return new ShelfExportDto(
                shelf.getName(),
                shelf.getType().name(),
                books);
    }

    private BookExportDto mapShelfBook(ShelfBook shelfBook) {
        Book book = shelfBook.getBook();
        return new BookExportDto(
                book.getTitle(),
                book.getAuthor() != null ? book.getAuthor().getName() : "",
                book.getIsbn(),
                book.getPublicationYear(),
                shelfBook.getDateAdded() != null ? shelfBook.getDateAdded().format(DATE_FORMATTER) : "");
    }

    private ReviewExportDto mapReview(Review review) {
        Book book = review.getBook();
        return new ReviewExportDto(
                book.getTitle(),
                book.getAuthor() != null ? book.getAuthor().getName() : "",
                review.getRating(),
                review.getComment(),
                review.getCreatedAt() != null ? review.getCreatedAt().format(DATE_FORMATTER) : "");
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
