package com.example.goodreads.repository;

import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.dto.book.PopularBookResponse;
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
    SELECT sb.book, COALESCE(AVG(r.rating), 0) as avgRating
    FROM Shelf s
    JOIN s.shelfBooks sb
    JOIN sb.book b
    LEFT JOIN b.reviews r
    WHERE s.id = :shelfId
    GROUP BY b
    ORDER BY b.id
""")
    List<Object[]> findBooksWithAverageRatingByShelfId(@Param("shelfId") Long shelfId);

    // ------------------- BOOK BY ID WITH AVERAGE RATING -------------------
    @Query("""
    SELECT b, COALESCE(AVG(r.rating), 0) as avgRating
    FROM Book b
    LEFT JOIN b.reviews r
    WHERE b.id = :bookId
    GROUP BY b
""")
    List<Object[]> findBookByIdWithAverageRating(@Param("bookId") Long bookId);


    // ------------------- BOOKS SEARCHED BY TITLE, AUTHOR OR ISBN WITH AVERAGE RATING WITHOUT PAGINATION -------------------
    @Query("""
    SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<Book> searchByTitleOrAuthorOrIsbn(@Param("query") String query);

    // ------------------- ALL BOOKS WITH AUTHORS (AVOIDS N + 1 QUERY PROBLEM) -------------------
    @Query("SELECT b FROM Book b JOIN FETCH b.author ORDER BY b.id ASC")
    List<Book> findAllWithAuthor();

    // ------------------- FIND BOOK BY TITLE IGNORE CASE WITH AUTHOR -------------------
    @Query("SELECT b FROM Book b JOIN FETCH b.author WHERE UPPER(b.title) = UPPER(:title)")
    Optional<Book> findByTitleIgnoreCaseWithAuthor(@Param("title") String title);

    // ---- TOTAL BOOK READERS ----
    @Query("""
    SELECT COUNT(DISTINCT(sb.shelf.user.id)) FROM Book b
    JOIN b.shelfBooks sb
    WHERE b.id = :bookId AND sb.shelf.type IN ('READ', 'CURRENTLY_READING')
""")
    Long countTotalReadersByBookId(@Param("bookId") Long bookId);

    @Query("""
    SELECT new com.example.goodreads.dto.book.PopularBookResponse(b.id, b.title, b.author.name, COALESCE(CAST(AVG(r.rating) AS double), 0.0), COUNT(DISTINCT sb.id)) as readers FROM Book b
    JOIN b.shelfBooks sb
    LEFT JOIN b.reviews r
    WHERE sb.shelf.type IN ('READ', 'CURRENTLY_READING')
    AND sb.dateAdded >= :dateFrom
    GROUP BY b.id, b.title, b.author.name
    ORDER BY COUNT(DISTINCT sb.id) DESC
    LIMIT 10
""")
    List<PopularBookResponse> findTop10BooksByMostReadsAfterDate(@Param("dateFrom") java.time.LocalDateTime dateFrom);
}
