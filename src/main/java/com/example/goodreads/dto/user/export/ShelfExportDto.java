package com.example.goodreads.dto.user.export;

import java.util.List;

public class ShelfExportDto {
    private String name;
    private String type;
    private List<BookExportDto> books;

    public ShelfExportDto() {
    }

    public ShelfExportDto(String name, String type, List<BookExportDto> books) {
        this.name = name;
        this.type = type;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BookExportDto> getBooks() {
        return books;
    }

    public void setBooks(List<BookExportDto> books) {
        this.books = books;
    }
}
