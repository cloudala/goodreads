package com.example.goodreads.repository;

import com.example.goodreads.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    // ------------------- ALL BOOKS WITH PAGINATION + AVERAGE RATING -------------------
    @Query("""
        SELECT b, COALESCE(AVG(r.rating), 0) as avgRating
        FROM Book b
        LEFT JOIN b.reviews r
        GROUP BY b
        ORDER BY b.id
    """)
    Page<Object[]> findAllBooksWithAverageRating(Pageable pageable);

    // ------------------- BOOKS SEARCHED BY TITLE, AUTHOR OR ISBN WITH AVERAGE RATING AND PAGINATION -------------------
    @Query("""
    SELECT b, COALESCE(AVG(r.rating), 0) as avgRating
    FROM Book b
    LEFT JOIN b.reviews r
    WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
    GROUP BY b
    ORDER BY b.id
""")
    Page<Object[]> searchBooksWithAverageRating(@Param("query") String query, Pageable pageable);

    // ------------------- BOOKS BY SHELF ID WITH AVERAGE RATING -------------------
    @Query("""
    SELECT b, COALESCE(AVG(r.rating), 0) as avgRating
    FROM Shelf s
    JOIN s.books b
    LEFT JOIN b.reviews r
    WHERE s.id = :shelfId
    GROUP BY b
    ORDER BY b.id
""")
    List<Object[]> findBooksWithAverageRatingByShelfId(@Param("shelfId") Long shelfId);

    // ------------------- BOOKS SEARCHED BY TITLE, AUTHOR OR ISBN WITH AVERAGE RATING WITHOUT PAGINATION -------------------
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :query, '%'))" +
            "OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchByTitleOrAuthorOrIsbn(@Param("query") String query);

    // ------------------- ALL BOOKS WITH AUTHORS (AVOIDS N + 1 QUERY PROBLEM) -------------------
    @Query("SELECT b FROM Book b JOIN FETCH b.author ORDER BY b.id ASC")
    List<Book> findAllWithAuthor();

    // ------------------- FIND BOOK BY TITLE IGNORE CASE WITH AUTHOR -------------------
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE UPPER(b.title) = UPPER(:title)")
    Optional<Book> findByTitleIgnoreCaseWithAuthor(@Param("title") String title);

}
