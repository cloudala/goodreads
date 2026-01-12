package com.example.goodreads.exception;

public class InvalidShelfActionException extends RuntimeException {
    public InvalidShelfActionException(String message) {
        super(message);
    }
}
