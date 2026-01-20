package com.example.goodreads.repository;

import com.example.goodreads.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ShelfRepositoryTest {

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    void setUp() {
        // Create and persist a user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@gmail.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        testEntityManager.persist(user);

        testEntityManager.flush();
    }

    @Test
    void testFindByUserId() {
        // Create shelves
        Shelf shelf1 = new Shelf();
        shelf1.setName("Favorites");
        shelf1.setUser(user);
        testEntityManager.persist(shelf1);

        Shelf shelf2 = new Shelf();
        shelf2.setName("To Read");
        shelf2.setUser(user);
        testEntityManager.persist(shelf2);

        testEntityManager.flush();

        // Test repository method
        List<Shelf> shelves = shelfRepository.findByUserId(user.getId());
        assertEquals(2, shelves.size());
    }

    @Test
    void testFindByIdAndUserId() {
        Shelf shelf = new Shelf();
        shelf.setName("Currently Reading");
        shelf.setUser(user);
        testEntityManager.persist(shelf);
        testEntityManager.flush();

        Optional<Shelf> foundShelf = shelfRepository.findByIdAndUserId(shelf.getId(), user.getId());
        assertTrue(foundShelf.isPresent());
        assertEquals("Currently Reading", foundShelf.get().getName());
        assertThat(foundShelf.get().getUser().getUsername()).isEqualTo("testuser");
    }
}
