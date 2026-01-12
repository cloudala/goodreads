package com.example.goodreads.dto.shelf;

import com.example.goodreads.model.ShelfAction;

public class ShelfActionRequest {
    private Long bookId;
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
