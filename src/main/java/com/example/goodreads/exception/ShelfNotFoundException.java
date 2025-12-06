package com.example.goodreads.exception;

public class ShelfNotFoundException extends RuntimeException {
    public ShelfNotFoundException(String message) {
        super(message);
    }
}
