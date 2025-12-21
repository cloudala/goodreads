package com.example.goodreads.dto.admin.review;

import java.time.LocalDateTime;

public class AdminReviewResponse {
    Long id;
    int rating;
    String comment;
    String username;
    String bookTitle;
    LocalDateTime createdAt;

    public AdminReviewResponse() {}

    public AdminReviewResponse(Long id, int rating, String comment, String username, String bookTitle, LocalDateTime createdAt) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.username = username;
        this.bookTitle = bookTitle;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
