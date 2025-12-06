package com.example.goodreads.repository;

import com.example.goodreads.model.Shelf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShelfRepository extends JpaRepository<Shelf, Long> {
    List<Shelf> findByUserId(Long userId);

    Optional<Shelf> findByIdAndUserId(Long shelfId, Long id);
}
