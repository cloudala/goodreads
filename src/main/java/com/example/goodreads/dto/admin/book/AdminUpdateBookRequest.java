package com.example.goodreads.dto.admin.book;

import com.example.goodreads.validation.PubYearPastOrPresent;

public class AdminUpdateBookRequest {
    private String title;
    private String author;
    private String isbn;
    @PubYearPastOrPresent
    private Integer publicationYear;

    public AdminUpdateBookRequest() {}

    public AdminUpdateBookRequest(String title, String author, String isbn, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
}
