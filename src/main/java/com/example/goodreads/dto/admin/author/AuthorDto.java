package com.example.goodreads.dto.admin.author;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthorDto {
    @NotNull(message = "Author ID is required")
    private Long id;
    @NotBlank(message = "Author name is required")
    private String name;

    public AuthorDto() {
    }

    public AuthorDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
