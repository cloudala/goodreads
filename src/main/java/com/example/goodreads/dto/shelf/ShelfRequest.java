package com.example.goodreads.dto.shelf;

import jakarta.validation.constraints.NotBlank;

public class ShelfRequest {
    @NotBlank(message = "Shelf name is required")
    private String name;

    public ShelfRequest() {
    }

    public ShelfRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
