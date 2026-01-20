package com.example.goodreads.repository;

import com.example.goodreads.model.Shelf;
import com.example.goodreads.model.ShelfType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    List<Shelf> findByUserId(Long userId);

    Optional<Shelf> findByIdAndUserId(Long shelfId, Long id);

    Optional<Shelf> findByUserIdAndType(Long userId, ShelfType type);

    @Query("""
            SELECT s FROM Shelf s
            LEFT JOIN FETCH s.shelfBooks
            WHERE s.user.id = :userId AND s.type = :type
            """)
    Optional<Shelf> findByUserIdAndTypeWithBooks(Long userId, ShelfType type);

    // ---- Count total books in shelf ----
    @Query("""
            SELECT COUNT(DISTINCT sb) FROM Shelf s
            JOIN s.shelfBooks sb
            WHERE s.type = :shelfType AND s.user.id = :userId
            """)
    long countTotalBooksByShelfTypeAndUserId(ShelfType shelfType, Long userId);

    // ---- Count total books added after a specific date in shelf ----
    @Query("""
            SELECT COUNT(sb) FROM Shelf s
            JOIN s.shelfBooks sb
            WHERE s.type = :shelfType AND s.user.id = :userId
            AND CAST(sb.dateAdded AS date) > :date
            """)
    long countBooksByShelfTypeAndDateAddedAfter(ShelfType shelfType, Long userId, LocalDate date);

}
