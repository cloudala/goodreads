package com.example.goodreads.dto.user.export;

import java.util.List;

public class UserExportDto {
    private String username;
    private String email;
    private List<ShelfExportDto> shelves;
    private List<ReviewExportDto> reviews;
    private String exportedAt;

    public UserExportDto() {
    }

    public UserExportDto(String username, String email, List<ShelfExportDto> shelves,
            List<ReviewExportDto> reviews, String exportedAt) {
        this.username = username;
        this.email = email;
        this.shelves = shelves;
        this.reviews = reviews;
        this.exportedAt = exportedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ShelfExportDto> getShelves() {
        return shelves;
    }

    public void setShelves(List<ShelfExportDto> shelves) {
        this.shelves = shelves;
    }

    public List<ReviewExportDto> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewExportDto> reviews) {
        this.reviews = reviews;
    }

    public String getExportedAt() {
        return exportedAt;
    }

    public void setExportedAt(String exportedAt) {
        this.exportedAt = exportedAt;
    }
}
