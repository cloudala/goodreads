package com.example.goodreads.config.init;

import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.model.*;
import com.example.goodreads.repository.*;
import com.example.goodreads.service.user.ShelfService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional  // important for lazy loading relationships
    public CommandLineRunner loadData(
            UserRepository userRepository,
            ShelfService shelfService,
            ShelfRepository shelfRepository,
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            ReviewRepository reviewRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // ---------- USERS ----------
                User alice = new User();
                alice.setUsername("alice");
                alice.setEmail("alice@example.com");
                alice.setPassword(passwordEncoder.encode("password"));
                alice.setRole(Role.USER);
                userRepository.save(alice);

                User bob = new User();
                bob.setUsername("bob");
                bob.setEmail("bob@example.com");
                bob.setPassword(passwordEncoder.encode("password"));
                bob.setRole(Role.USER);
                userRepository.save(bob);

                // ---------- SHELVES ----------
                Shelf favorites = new Shelf("Favorites");
                Shelf toRead = new Shelf("To Read");

                alice.addShelf(favorites);
                alice.addShelf(toRead);

                shelfRepository.save(favorites);
                shelfRepository.save(toRead);

                // ---------- BOOKS ----------
                Book pride = bookRepository.findByTitleIgnoreCaseWithAuthor("Pride and Prejudice")
                        .orElseThrow(() -> new BookNotFoundException("Book not found: Pride and Prejudice"));
                Book gatsby = bookRepository.findByTitleIgnoreCaseWithAuthor("The Great Gatsby")
                        .orElseThrow(() -> new BookNotFoundException("Book not found: The Great Gatsby"));
                Book mockingbird = bookRepository.findByTitleIgnoreCaseWithAuthor("To Kill a Mockingbird")
                        .orElseThrow(() -> new BookNotFoundException("Book not found: To Kill a Mockingbird"));

                // ---------- RELATIONSHIPS: Add books to shelves ----------
                favorites.addBook(pride);
                favorites.addBook(mockingbird);
                toRead.addBook(gatsby);

                // ---------- REVIEWS ----------
                Review r1 = new Review(5, "Absolutely loved it!", alice, pride);
                Review r2 = new Review(4, "Great book, a classic.", bob, pride);
                Review r3 = new Review(3, "Interesting, but a bit dated.", alice, gatsby);
                Review r4 = new Review(5, "Must-read!", bob, mockingbird);

                reviewRepository.save(r1);
                reviewRepository.save(r2);
                reviewRepository.save(r3);
                reviewRepository.save(r4);

                System.out.println("🌱 Initial test data loaded successfully.");
        };
    }
}
