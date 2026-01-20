package com.example.goodreads.repository;

import com.example.goodreads.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);

    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);

    // ---- BOOK REVIEW DISTRIBUTION ----
    @Query("""
    SELECT r.rating, COUNT(r) FROM Review r
    WHERE r.book.id = :bookId
    GROUP BY r.rating
    ORDER BY r.rating
""")
    List<Long[]> getReviewDistributionByBookId(@Param("bookId") Long bookId);
}
