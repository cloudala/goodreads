package com.example.goodreads.dto.shelf;

public class ShelfRequest {
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
