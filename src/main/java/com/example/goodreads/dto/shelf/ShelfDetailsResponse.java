package com.example.goodreads.dto.shelf;

import com.example.goodreads.dto.book.BookResponse;

import java.util.List;

public class ShelfDetailsResponse {
    private Long id;
    private String name;
    private List<BookResponse> books;

    public ShelfDetailsResponse() {
    }

    public ShelfDetailsResponse(Long id, String name, List<BookResponse> books) {
        this.id = id;
        this.name = name;
        this.books = books;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BookResponse> getBooks() {
        return books;
    }

    public void setBooks(List<BookResponse> books) {
        this.books = books;
    }
}
