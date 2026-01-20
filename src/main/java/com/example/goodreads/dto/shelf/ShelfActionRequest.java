package com.example.goodreads.dto.shelf;

import com.example.goodreads.model.ShelfAction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ShelfActionRequest {
    @NotNull(message = "Book ID is required")
    private Long bookId;
    @NotNull(message = "Shelf action is required")
    private ShelfAction action;
    private Long destinationShelfId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public ShelfAction getAction() {
        return action;
    }

    public void setAction(ShelfAction action) {
        this.action = action;
    }

    public Long getDestinationShelfId() {
        return destinationShelfId;
    }

    public void setDestinationShelfId(Long destinationShelfId) {
        this.destinationShelfId = destinationShelfId;
    }
}
