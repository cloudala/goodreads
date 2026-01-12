package com.example.goodreads.repository;

import com.example.goodreads.model.Author;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Review;
import com.example.goodreads.model.Shelf;
import com.example.goodreads.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    // ---------- HELPERS ----------

    private Book persistBook(String title, String isbn, Author author) {
        // Book constructor in model is (String title, Author author, String isbn, int publicationYear)
        Book book = new Book(title, author, isbn, 0);
        entityManager.persist(book);
        return book;
    }

    private void addReview(Book book, int rating) {
        // Review constructor is (int rating, String comment, User user, Book book)
        User user = new User(null, "testuser", "test@example.com", "password");
        entityManager.persist(user);
        Review review = new Review(rating, "ok", user, book);
        entityManager.persist(review);
    }

    // =========================================================
    // CRUD (min. kilka testów, nawet jeśli save() jest trywialne)
    // =========================================================

    @Test
    void shouldSaveAndFindBookById() {
        Author author = new Author("Joshua Bloch");
        entityManager.persist(author);

        Book book = persistBook("Effective Java", "123", author);
        entityManager.flush();

        Optional<Book> found = bookRepository.findById(book.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void shouldDeleteBook() {
        Author author = new Author("Martin Fowler");
        entityManager.persist(author);

        Book book = persistBook("Refactoring", "456", author);
        entityManager.flush();

        bookRepository.deleteById(book.getId());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    // =========================================================
    // CUSTOM QUERY: findAllBooksWithAverageRating (Page<Object[]>)
    // =========================================================

    @Test
    void shouldReturnAllBooksWithAverageRating() {
        Author author = new Author("Autor A");
        entityManager.persist(author);

        Book book1 = persistBook("Book 1", "111", author);
        persistBook("Book 2", "222", author);

        addReview(book1, 5);
        addReview(book1, 3);

        entityManager.flush();

        Page<Object[]> page =
                bookRepository.findAllBooksWithAverageRating(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);

        Object[] row = page.getContent().get(0);
        Book book = (Book) row[0];
        Double avgRating = (Double) row[1];

        if (book.getTitle().equals("Book 1")) {
            assertThat(avgRating).isEqualTo(4.0);
        }
    }

    @Test
    void shouldReturnZeroAverageRatingWhenNoReviews() {
        Author author = new Author("Autor B");
        entityManager.persist(author);

        persistBook("No Reviews", "333", author);
        entityManager.flush();

        Page<Object[]> page =
                bookRepository.findAllBooksWithAverageRating(PageRequest.of(0, 10));

        Object[] row = page.getContent().get(0);
        Double avgRating = (Double) row[1];

        assertThat(avgRating).isEqualTo(0.0);
    }

    // =========================================================
    // SEARCH WITH AVG RATING + PAGINATION
    // =========================================================

    @Test
    void shouldSearchBooksByTitleIgnoringCase() {
        Author author = new Author("Robert Martin");
        entityManager.persist(author);

        persistBook("Clean Code", "AAA", author);
        persistBook("Clean Architecture", "BBB", author);

        entityManager.flush();

        Page<Object[]> result =
                bookRepository.searchBooksWithAverageRating(
                        "clean",
                        PageRequest.of(0, 10)
                );

        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    // =========================================================
    // SEARCH WITHOUT PAGINATION
    // =========================================================

    @Test
    void shouldSearchByIsbnOrAuthorName() {
        Author author = new Author("Kent Beck");
        entityManager.persist(author);

        persistBook("TDD", "ISBN-999", author);
        entityManager.flush();

        List<Book> result =
                bookRepository.searchByTitleOrAuthorOrIsbn("kent");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor().getName()).isEqualTo("Kent Beck");
    }

    // =========================================================
    // FETCH JOIN (N + 1 PROTECTION)
    // =========================================================

    @Test
    void shouldFetchBooksWithAuthorsInSingleQuery() {
        Author author = new Author("Eric Evans");
        entityManager.persist(author);

        persistBook("DDD", "DDD-1", author);
        entityManager.flush();

        List<Book> books = bookRepository.findAllWithAuthor();

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAuthor().getName()).isEqualTo("Eric Evans");
    }

    // =========================================================
    // FIND BY TITLE IGNORE CASE WITH AUTHOR
    // =========================================================

    @Test
    void shouldFindBookByTitleIgnoreCaseWithAuthor() {
        Author author = new Author("Craig Walls");
        entityManager.persist(author);

        persistBook("Spring in Action", "SPR-1", author);
        entityManager.flush();

        Optional<Book> result =
                bookRepository.findByTitleIgnoreCaseWithAuthor("spring IN action");

        assertThat(result).isPresent();
        assertThat(result.get().getAuthor().getName()).isEqualTo("Craig Walls");
    }

    // =========================================================
    // BOOKS BY SHELF
    // =========================================================

    @Test
    void shouldFindBooksByShelfIdWithAverageRating() {
        Author author = new Author("Autor Shelf");
        entityManager.persist(author);

        Book book = persistBook("Shelf Book", "S1", author);

        Shelf shelf = new Shelf("Favorites");
        shelf.getShelfBooks().add(book);

        entityManager.persist(shelf);
        entityManager.flush();

        List<Object[]> result =
                bookRepository.findBooksWithAverageRatingByShelfId(shelf.getId());

        assertThat(result).hasSize(1);
    }
}
