package com.example.goodreads.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "shelves")
public class Shelf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShelfType type = ShelfType.CUSTOM;


    // ------------ USER RELATION ------------
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // ------------ BOOK RELATION ------------
    @OneToMany(mappedBy = "shelf", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShelfBook> shelfBooks = new HashSet<>();

    public Shelf() {}

    public Shelf(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShelfType getType() {
        return type;
    }

    public void setType(ShelfType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<ShelfBook> getShelfBooks() {
        return shelfBooks;
    }

    public void setShelfBooks(Set<ShelfBook> books) {
        this.shelfBooks = books;
    }

    // -------- Helper methods --------
    public void addBook(Book book) {
        ShelfBook shelfBook = new ShelfBook(this, book);
        shelfBooks.add(shelfBook);
        // book.getShelves().add(this);
    }

    public void removeBook(Book book) {
        shelfBooks.removeIf(shelfBook -> shelfBook.getBook().equals(book));
        // book.getShelves().remove(this);
    }
}
