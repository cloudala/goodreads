package com.example.goodreads.dto.book;

public class PopularBookResponse {
    private Long id;
    private String title;
    private String author;
    private Double averageRating;
    private Long readersLastMonth;

    public PopularBookResponse(Long id, String title, String author, Double averageRating, Long readersLastMonth) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.averageRating = averageRating;
        this.readersLastMonth = readersLastMonth;
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

    public Long getReadersLastMonth() {
        return readersLastMonth;
    }

    public void setReadersLastMonth(Long readersLastMonth) {
        this.readersLastMonth = readersLastMonth;
    }
}
