package com.example.goodreads.repository;

import com.example.goodreads.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setName("Test Author");
        testEntityManager.persist(author);

        testEntityManager.flush();
    }

    @Test
    void testFindByNameIgnoreCaseFound() {
        Optional<Author> foundAuthor = authorRepository.findByNameIgnoreCase("Test Author");
        assertTrue(foundAuthor.isPresent());
        assertEquals("Test Author", foundAuthor.get().getName());
    }

    @Test
    void testFindByNameIgnoreCaseDifferentCase() {
        Optional<Author> foundAuthor = authorRepository.findByNameIgnoreCase("test author");
        assertTrue(foundAuthor.isPresent());
        assertThat(foundAuthor.get().getName()).isEqualTo("Test Author");
    }

    @Test
    void testFindByNameIgnoreCaseNotFound() {
        Optional<Author> foundAuthor = authorRepository.findByNameIgnoreCase("Non Existent");
        assertFalse(foundAuthor.isPresent());
    }
}
