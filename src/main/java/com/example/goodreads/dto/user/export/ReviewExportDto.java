package com.example.goodreads.dto.user.export;

public class ReviewExportDto {
    private String bookTitle;
    private String bookAuthor;
    private int rating;
    private String comment;
    private String createdAt;

    public ReviewExportDto() {
    }

    public ReviewExportDto(String bookTitle, String bookAuthor, int rating, String comment, String createdAt) {
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
