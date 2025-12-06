package com.example.goodreads.config;

import com.example.goodreads.model.*;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.repository.ShelfRepository;
import com.example.goodreads.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadData(
            UserRepository userRepository,
            ShelfRepository shelfRepository,
            BookRepository bookRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // ---------- USERS ----------
            User alice = new User();
            alice.setUsername("alice");
            alice.setEmail("alice@example.com");
            alice.setPassword(passwordEncoder.encode("password"));
            alice.setRole(Role.USER);

            User bob = new User();
            bob.setUsername("bob");
            bob.setEmail("bob@example.com");
            bob.setPassword(passwordEncoder.encode("password"));
            bob.setRole(Role.USER);

            userRepository.save(alice);
            userRepository.save(bob);

            // ---------- BOOKS ----------
            Book book1 = new Book("The Hobbit", "J.R.R. Tolkien", "9780261103344", 1937);
            Book book2 = new Book("1984", "George Orwell", "9780451524935", 1949);
            Book book3 = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008);

            bookRepository.save(book1);
            bookRepository.save(book2);
            bookRepository.save(book3);

            // ---------- SHELVES ----------
            Shelf shelf1 = new Shelf("Favorites");
            Shelf shelf2 = new Shelf("To Read");

            // assign shelves to users
            alice.addShelf(shelf1);
            alice.addShelf(shelf2);

            // ---------- RELATIONSHIPS: Add books to shelves ----------
            shelf1.addBook(book1); // favorites has The Hobbit
            shelf1.addBook(book3); // and Clean Code

            shelf2.addBook(book2); // to-read has 1984

            shelfRepository.save(shelf1);
            shelfRepository.save(shelf2);

            System.out.println("🌱 Initial test data loaded.");
        };
    }
}
