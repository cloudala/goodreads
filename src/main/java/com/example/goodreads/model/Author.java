package com.example.goodreads.model;

import jakarta.persistence.*;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // opcjonalnie (na później)
    // @OneToMany(mappedBy = "author")
    // private Set<Book> books = new HashSet<>();

    public Author() {}

    public Author(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public  void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

