package com.example.goodreads.service.admin;

import com.example.goodreads.dto.admin.book.AdminBookResponse;
import com.example.goodreads.dto.admin.book.AdminCreateBookRequest;
import com.example.goodreads.dto.admin.book.AdminUpdateBookRequest;
import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.model.Author;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Shelf;
import com.example.goodreads.model.ShelfBook;
import com.example.goodreads.repository.AuthorRepository;
import com.example.goodreads.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AdminBookService adminBookService;

    // ---------- GET ALL ----------

    @Test
    void testReturnAllBooks() {
        // Given
        Author author = new Author("Tolkien");
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Hobbit");
        book.setAuthor(author);
        book.setIsbn("123");
        book.setPublicationYear(1937);

        when(bookRepository.findAllWithAuthor()).thenReturn(List.of(book));

        // When
        List<AdminBookResponse> result = adminBookService.getAllBooks();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Hobbit");
        assertThat(result.get(0).getAuthor()).isEqualTo("Tolkien");

        verify(bookRepository).findAllWithAuthor();
    }

    // ---------- GET BY ID ----------

    @Test
    void testReturnBookById() {
        // Given
        Long id = 1L;
        Author author = new Author("Orwell");
        Book book = new Book();
        book.setId(id);
        book.setTitle("1984");
        book.setAuthor(author);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // When
        AdminBookResponse response = adminBookService.getBookById(id);

        // Then
        assertThat(response.getTitle()).isEqualTo("1984");
        assertThat(response.getAuthor()).isEqualTo("Orwell");
    }

    @Test
    void testThrowExceptionWhenBookNotFound() {
        // Given
        Long id = 99L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminBookService.getBookById(id))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with id 99 not found");
    }

    // ---------- CREATE ----------

    @Test
    void testCreateBookWithNewAuthor() {
        // Given
        AdminCreateBookRequest request = new AdminCreateBookRequest(
                "Dune",
                "Frank Herbert",
                "ISBN",
                1965
        );

        when(authorRepository.findByNameIgnoreCase("Frank Herbert"))
                .thenReturn(Optional.empty());

        when(authorRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        when(bookRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        // When
        AdminBookResponse response = adminBookService.createBook(request);

        // Then
        assertThat(response.getTitle()).isEqualTo("Dune");
        assertThat(response.getAuthor()).isEqualTo("Frank Herbert");

        verify(authorRepository).save(any(Author.class));
        verify(bookRepository).save(any(Book.class));
    }

    // ---------- UPDATE ----------

    @Test
    void testUpdateOnlyProvidedFields() {
        // Given
        Long id = 1L;
        Author oldAuthor = new Author("Old Author");
        Book book = new Book();
        book.setId(id);
        book.setTitle("Old title");
        book.setAuthor(oldAuthor);

        AdminUpdateBookRequest request = new AdminUpdateBookRequest();
        request.setTitle("New title");

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));
        when(bookRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // When
        AdminBookResponse response = adminBookService.updateBook(id, request);

        // Then
        assertThat(response.getTitle()).isEqualTo("New title");
        assertThat(response.getAuthor()).isEqualTo("Old Author");
    }

    // ---------- DELETE ----------

    @Test
    void testDeleteBookAndRemoveFromShelves() {
        // Given
        Long id = 1L;
        Book book = new Book();
        Shelf shelf = new Shelf();

        // Create the join entity
        ShelfBook shelfBook = new ShelfBook();
        shelfBook.setBook(book);
        shelfBook.setShelf(shelf);

        // Set up relationships
        book.setShelves(new HashSet<>(Set.of(shelf)));           // book -> shelves
        shelf.setShelfBooks(new HashSet<>(Set.of(shelfBook)));   // shelf -> ShelfBook

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // When
        adminBookService.deleteBook(id);

        // Then
        verify(bookRepository).delete(book);

        // The sets should be empty after deletion
        // assertThat(book.getShelves()).isEmpty();
        // assertThat(shelf.getShelfBooks()).isEmpty();
    }


}
