package com.example.goodreads.service.user;

import com.example.goodreads.dto.shelf.ShelfDetailsResponse;
import com.example.goodreads.dto.shelf.ShelfRequest;
import com.example.goodreads.dto.shelf.ShelfResponse;
import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.exception.ShelfNotFoundException;
import com.example.goodreads.model.*;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.repository.ShelfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ShelfService shelfService;

    private User user;
    private Shelf shelf;
    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        shelf = new Shelf("My Shelf");
        shelf.setId(1L);
        shelf.setUser(user);
        shelf.setType(ShelfType.CUSTOM);

        author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor(author);
    }

    // --------------------------------------------------
    // createDefaultShelves
    // --------------------------------------------------

    @Test
    void testCreateDefaultShelves() {
        shelfService.createDefaultShelves(user);

        assertEquals(3, user.getShelves().size());

        verifyNoInteractions(shelfRepository, bookRepository, userService);
    }

    // --------------------------------------------------
    // getUserShelves
    // --------------------------------------------------

    @Test
    void testGetUserShelves() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByUserId(1L)).thenReturn(List.of(shelf));

        List<ShelfResponse> shelves = shelfService.getUserShelves("testuser");

        assertEquals(1, shelves.size());

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByUserId(1L);
    }

    // --------------------------------------------------
    // getShelfById
    // --------------------------------------------------

    @Test
    void testGetShelfById() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));

        Object[] row = new Object[] { book, 4.5 };
        when(bookRepository.findBooksWithAverageRatingByShelfId(1L))
                .thenReturn(List.<Object[]>of(row));

        ShelfDetailsResponse response = shelfService.getShelfById("testuser", 1L);

        assertEquals("My Shelf", response.getName());
        assertEquals(1, response.getBooks().size());

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(bookRepository).findBooksWithAverageRatingByShelfId(1L);
    }

    @Test
    void testGetShelfByIdNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ShelfNotFoundException.class,
                () -> shelfService.getShelfById("testuser", 1L));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verifyNoInteractions(bookRepository);
    }

    // --------------------------------------------------
    // addShelfToUser
    // --------------------------------------------------

    @Test
    void testAddShelfToUser() {
        ShelfRequest request = new ShelfRequest();
        request.setName("New Shelf");

        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.save(any(Shelf.class))).thenAnswer(invocation -> {
            Shelf saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        ShelfResponse response = shelfService.addShelfToUser("testuser", request);

        assertEquals("New Shelf", response.getName());

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).save(any(Shelf.class));
    }

    // --------------------------------------------------
    // updateUserShelf
    // --------------------------------------------------

    @Test
    void testUpdateUserShelf() {
        ShelfRequest request = new ShelfRequest();
        request.setName("Updated Shelf");

        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.save(shelf)).thenReturn(shelf);

        ShelfResponse response = shelfService.updateUserShelf("testuser", 1L, request);

        assertEquals("Updated Shelf", response.getName());

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository).save(shelf);
    }

    @Test
    void testUpdateBuiltInShelfForbidden() {
        shelf.setType(ShelfType.READ);

        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));

        assertThrows(IllegalArgumentException.class,
                () -> shelfService.updateUserShelf("testuser", 1L, new ShelfRequest()));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository, never()).save(any());
    }

    // --------------------------------------------------
    // deleteUserShelf
    // --------------------------------------------------

    @Test
    void testDeleteUserShelf() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));

        shelfService.deleteUserShelf("testuser", 1L);

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository).delete(shelf);
    }

    @Test
    void testDeleteBuiltInShelfForbidden() {
        shelf.setType(ShelfType.READ);

        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));

        assertThrows(IllegalArgumentException.class,
                () -> shelfService.deleteUserShelf("testuser", 1L));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository, never()).delete(any());
    }

    // --------------------------------------------------
    // add / remove book
    // --------------------------------------------------

    @Test
    void testAddBookToShelf() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        shelfService.addBookToShelf("testuser", 1L, 1L);

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(bookRepository).findById(1L);
        verify(shelfRepository).save(shelf);
    }

    @Test
    void testAddBookToShelfBookNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> shelfService.addBookToShelf("testuser", 1L, 1L));

        verify(userService).getCurrentUser("testuser");
        verify(bookRepository).findById(1L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testRemoveBookFromShelf() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        shelfService.removeBookFromShelf("testuser", 1L, 1L);

        verify(userService).getCurrentUser("testuser");
        verify(bookRepository).findById(1L);
        verify(shelfRepository).save(shelf);
    }

    // --------------------------------------------------
    // move book between shelves
    // --------------------------------------------------

    @Test
    void testMoveBookFromShelfToShelf() {
        Shelf toShelf = new Shelf("Other Shelf");
        toShelf.setId(2L);
        toShelf.setUser(user);
        toShelf.setType(ShelfType.CUSTOM);

        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(toShelf));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        shelfService.moveBookFromShelfToShelf("testuser", 1L, 2L, 1L);

        verify(userService).getCurrentUser("testuser");
        verify(bookRepository).findById(1L);
        verify(shelfRepository).save(shelf);
        verify(shelfRepository).save(toShelf);
    }

    @Test
    void testMoveBookFromShelfToShelfSourceNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ShelfNotFoundException.class,
                () -> shelfService.moveBookFromShelfToShelf("testuser", 1L, 2L, 1L));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testMoveBookFromShelfToShelfDestinationNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(ShelfNotFoundException.class,
                () -> shelfService.moveBookFromShelfToShelf("testuser", 1L, 2L, 1L));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository).findByIdAndUserId(2L, 1L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testMoveBookFromShelfToShelfBookNotFound() {
        Shelf toShelf = new Shelf("Other Shelf");
        toShelf.setId(2L);
        toShelf.setUser(user);
        toShelf.setType(ShelfType.CUSTOM);

        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(shelfRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(toShelf));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> shelfService.moveBookFromShelfToShelf("testuser", 1L, 2L, 1L));

        verify(userService).getCurrentUser("testuser");
        verify(bookRepository).findById(1L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testRemoveBookFromShelfBookNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shelf));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> shelfService.removeBookFromShelf("testuser", 1L, 1L));

        verify(userService).getCurrentUser("testuser");
        verify(bookRepository).findById(1L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testAddBookToShelfShelfNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ShelfNotFoundException.class,
                () -> shelfService.addBookToShelf("testuser", 1L, 1L));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(bookRepository, never()).findById(any());
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testUpdateUserShelfNotFound() {
        ShelfRequest request = new ShelfRequest();
        request.setName("Updated Shelf");

        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ShelfNotFoundException.class,
                () -> shelfService.updateUserShelf("testuser", 1L, request));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository, never()).save(any());
    }

    @Test
    void testDeleteUserShelfNotFound() {
        when(userService.getCurrentUser("testuser")).thenReturn(user);
        when(shelfRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ShelfNotFoundException.class,
                () -> shelfService.deleteUserShelf("testuser", 1L));

        verify(userService).getCurrentUser("testuser");
        verify(shelfRepository).findByIdAndUserId(1L, 1L);
        verify(shelfRepository, never()).delete(any());
    }
}
