package com.example.goodreads.dto.book;

import java.util.Map;

public class BookStatsResponse {
    private Long bookId;
    private Long totalReaders;
    private Map<Long, Long> ratingsDistribution;
    private Double averageRating;

    public BookStatsResponse(Long bookId, Long totalReaders, Map<Long, Long> ratingsDistribution, Double averageRating) {
        this.bookId = bookId;
        this.totalReaders = totalReaders;
        this.ratingsDistribution = ratingsDistribution;
        this.averageRating = averageRating;
    }

    public Long getTotalReaders() {
        return totalReaders;
    }

    public void setTotalReaders(Long totalReaders) {
        this.totalReaders = totalReaders;
    }

    public Map<Long, Long> getRatingsDistribution() {
        return ratingsDistribution;
    }

    public void setRatingsDistribution(Map<Long, Long> ratingsDistribution) {
        this.ratingsDistribution = ratingsDistribution;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
