package com.example.goodreads.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

//    @Column(nullable = false)
//    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;


    private String isbn;

    private int publicationYear;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<ShelfBook> shelfBooks = new HashSet<>();

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Review> reviews = new HashSet<>();

    public Book() {}

    public Book(String title, Author author, String isbn, int publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Set<Shelf> getShelves() {
        return shelfBooks.stream().map(shelfBook -> shelfBook.getShelf()).collect(Collectors.toSet());
    }

    public void setShelves(Set<Shelf> shelves) {
        shelves.forEach(shelf -> {
            ShelfBook newShelfBook = new ShelfBook(shelf, this);
            this.shelfBooks.add(newShelfBook);
        });
    }
}
