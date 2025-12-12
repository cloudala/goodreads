package com.example.goodreads.repository;

import com.example.goodreads.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Fetch all books with their average rating
    @Query("""
        SELECT b, COALESCE(AVG(r.rating), 0) as avgRating
        FROM Book b
        LEFT JOIN b.reviews r
        GROUP BY b
        ORDER BY b.id
    """)
    Page<Object[]> findAllBooksWithAverageRating(Pageable pageable);

    // Search books by title or author or isbn (case-insensitive, partial match) with average rating
    @Query("""
    SELECT b, COALESCE(AVG(r.rating), 0) as avgRating
    FROM Book b
    LEFT JOIN b.reviews r
    WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
    GROUP BY b
    ORDER BY b.id
""")
    Page<Object[]> searchBooksWithAverageRating(@Param("query") String query, Pageable pageable);

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

    // Search books by title or author or isbn (case-insensitive, partial match)
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))" +
            "OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Book> searchByTitleOrAuthorOrIsbn(@Param("query") String query);
}
