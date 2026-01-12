package com.example.goodreads.dto.book;

import com.example.goodreads.dto.review.ReviewResponse;

import java.util.List;

public class BookWithReviewsResponse {
    private Long id;
    private String title;
    private String author;
    private Double averageRating;
    private List<ReviewResponse> reviews;

    public BookWithReviewsResponse(Long id, String title, String author, Double averageRating, List<ReviewResponse> reviews) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.averageRating = averageRating;
        this.reviews = reviews;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }
}
