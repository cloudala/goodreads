package com.example.goodreads.service;

import com.example.goodreads.model.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    // Return a few featured books (hardcoded)
    public List<Book> getFeaturedBooks() {
        return List.of(
                new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", 1925),
                new Book("Pride and Prejudice", "Jane Austen", "9780141439518", 1813),
                new Book("1984", "George Orwell", "9780451524935", 1949)
        );
    }

    // Return a few recently added books (hardcoded)
    public List<Book> getRecentBooks() {
        return List.of(
                new Book("The Midnight Library", "Matt Haig", "9780525559474", 2020),
                new Book("Klara and the Sun", "Kazuo Ishiguro", "9780593318171", 2021),
                new Book("Project Hail Mary", "Andy Weir", "9780593135204", 2021),
                new Book("Dune", "Frank Herbert", "9780441013593", 1965)
        );
    }
}
