package com.example.goodreads.service.admin;

import com.example.goodreads.dao.AuthorDao;
import com.example.goodreads.dto.admin.author.AuthorDto;
import com.example.goodreads.exception.AuthorNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminAuthorServiceTest {

    private AuthorDao authorDao;
    private AdminAuthorService adminAuthorService;

    private AuthorDto sampleAuthor;

    @BeforeEach
    void setUp() {
        authorDao = mock(AuthorDao.class);
        adminAuthorService = new AdminAuthorService(authorDao);

        sampleAuthor = new AuthorDto(1L, "Test Author");
    }

    @Test
    void testGetAllAuthors() {
        when(authorDao.findAll()).thenReturn(List.of(sampleAuthor));

        List<AuthorDto> authors = adminAuthorService.getAllAuthors();

        assertNotNull(authors);
        assertEquals(1, authors.size());
        assertEquals("Test Author", authors.get(0).getName());

        verify(authorDao, times(1)).findAll();
    }

    @Test
    void testGetAuthorByIdSuccess() {
        when(authorDao.findById(1L)).thenReturn(Optional.of(sampleAuthor));

        AuthorDto author = adminAuthorService.getAuthorById(1L);

        assertNotNull(author);
        assertEquals(1L, author.getId());
        assertEquals("Test Author", author.getName());

        verify(authorDao, times(1)).findById(1L);
    }

    @Test
    void testGetAuthorByIdNotFound() {
        when(authorDao.findById(2L)).thenReturn(Optional.empty());

        AuthorNotFoundException exception = assertThrows(
                AuthorNotFoundException.class,
                () -> adminAuthorService.getAuthorById(2L)
        );

        assertEquals("Author with id 2 not found", exception.getMessage());
        verify(authorDao, times(1)).findById(2L);
    }

    @Test
    void testCreateAuthor() {
        adminAuthorService.createAuthor(sampleAuthor);

        verify(authorDao, times(1)).save(sampleAuthor);
    }

    @Test
    void testUpdateAuthor() {
        adminAuthorService.updateAuthor(1L, sampleAuthor);

        verify(authorDao, times(1)).update(1L, sampleAuthor);
    }

    @Test
    void testDeleteAuthor() {
        adminAuthorService.deleteAuthor(1L);

        verify(authorDao, times(1)).deleteById(1L);
    }
}
