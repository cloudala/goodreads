package com.example.goodreads.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shelf_books")
public class ShelfBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shelf_id")
    private Shelf shelf;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDateTime dateAdded = LocalDateTime.now();

    public ShelfBook() {
    }

    public ShelfBook(Long id, Shelf shelf, Book book, LocalDateTime dateAdded) {
        this.id = id;
        this.shelf = shelf;
        this.book = book;
        this.dateAdded = dateAdded;
    }

    public ShelfBook(Shelf shelf, Book book) {
        this.shelf = shelf;
        this.book = book;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
}
