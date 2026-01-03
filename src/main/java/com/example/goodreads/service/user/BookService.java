package com.example.goodreads.service.user;

import com.example.goodreads.dto.PaginatedResponse;
import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.model.Author;
import com.example.goodreads.model.Book;
import com.example.goodreads.repository.AuthorRepository;
import com.example.goodreads.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
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
        List<BookResponse> content = mapBooksWithAvgRating(results.getContent());
        return new PaginatedResponse<>(content, results.getNumber(), results.getSize(), results.getTotalElements(), results.getTotalPages());
    }

    public PaginatedResponse<BookResponse> searchBooks(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = bookRepository.searchBooksWithAverageRating(query, pageable);
        List<BookResponse> content = mapBooksWithAvgRating(results.getContent());
        return new PaginatedResponse<>(content, results.getNumber(), results.getSize(), results.getTotalElements(), results.getTotalPages());
    }

//    public List<BookResponse> getAllBooks() {
//        List<Book> books = bookRepository.findAll();
//        return books.stream()
//                .map(b -> new BookResponse(b.getId(), b.getTitle(), b.getAuthor(), 0.0))
//                .collect(Collectors.toList());
//    }

//    public List<BookResponse> searchBooks(String query) {
//        List<Book> books = bookRepository.searchByTitleOrAuthorOrIsbn(query);
//        return books.stream()
//                .map(b -> new BookResponse(b.getId(), b.getTitle(), b.getAuthor(), 0.0))
//                .collect(Collectors.toList());
//    }
}
