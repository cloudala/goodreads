package com.example.goodreads.repository;

import com.example.goodreads.model.Role;
import com.example.goodreads.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@gmail.com");
        user.setPassword("password");
        user.setRole(Role.USER);
        testEntityManager.persist(user);

        testEntityManager.flush();
    }

    @Test
    void testFindByUsernameFound() {
        Optional<User> foundUser = userRepository.findByUsername("testuser");
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertThat(foundUser.get().getEmail()).isEqualTo("testuser@gmail.com");
    }

    @Test
    void testFindByUsernameNotFound() {
        Optional<User> foundUser = userRepository.findByUsername("nonexistent");
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByUsernameTrue() {
        boolean exists = userRepository.existsByUsername("testuser");
        assertTrue(exists);
    }

    @Test
    void testExistsByUsernameFalse() {
        boolean exists = userRepository.existsByUsername("unknownuser");
        assertFalse(exists);
    }
}
