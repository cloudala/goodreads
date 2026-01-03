package com.example.goodreads.service.user;

import com.example.goodreads.dto.book.BookResponse;
import com.example.goodreads.dto.shelf.ShelfDetailsResponse;
import com.example.goodreads.dto.shelf.ShelfRequest;
import com.example.goodreads.dto.shelf.ShelfResponse;
import com.example.goodreads.exception.BookNotFoundException;
import com.example.goodreads.exception.ShelfNotFoundException;
import com.example.goodreads.model.Book;
import com.example.goodreads.model.Shelf;
import com.example.goodreads.model.ShelfType;
import com.example.goodreads.model.User;
import com.example.goodreads.repository.BookRepository;
import com.example.goodreads.repository.ShelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShelfService {
    private final ShelfRepository shelfRepository;
    private final BookRepository bookRepository;
    private final UserService userService;

    public ShelfService(ShelfRepository shelfRepository, BookRepository bookRepository, UserService userService) {
        this.shelfRepository = shelfRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
    }

    private List<BookResponse> mapBooksWithAvgRating(List<Object[]> results) {
        return results.stream()
                .map(obj -> {
                    Book book = (Book) obj[0];
                    Double avgRating = (Double) obj[1];
                    return new BookResponse(
                            book.getId(),
                            book.getTitle(),
                            book.getAuthor().getName(),
                            // book.getAuthor(),
                            avgRating
                    );
                })
                .toList();
    }


    public void createDefaultShelves(User user) {
        Shelf read = new Shelf("Read");
        read.setType(ShelfType.READ);

        Shelf currently = new Shelf("Currently Reading");
        currently.setType(ShelfType.CURRENTLY_READING);

        Shelf want = new Shelf("Want to Read");
        want.setType(ShelfType.WANT_TO_READ);

        user.addShelf(read);
        user.addShelf(currently);
        user.addShelf(want);
    }

    public List<ShelfResponse> getUserShelves(String username) {
        User user = userService.getCurrentUser(username);
        return shelfRepository.findByUserId(user.getId())
                .stream()
                .map(shelf -> new ShelfResponse(shelf.getId(), shelf.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ShelfDetailsResponse getShelfById(String username, Long shelfId) {
        User user = userService.getCurrentUser(username);

        Shelf shelf = shelfRepository.findByIdAndUserId(shelfId, user.getId())
                .orElseThrow(() -> new ShelfNotFoundException("Shelf not found"));

        List<Object[]> results = bookRepository.findBooksWithAverageRatingByShelfId(shelfId);

        List<BookResponse> bookResponses = mapBooksWithAvgRating(results);

        return new ShelfDetailsResponse(shelf.getId(), shelf.getName(), bookResponses);
    }

    public ShelfResponse addShelfToUser(String username, ShelfRequest shelfRequest) {
        User user = userService.getCurrentUser(username);
        Shelf shelf = new Shelf(shelfRequest.getName());
        shelf.setUser(user);
        Shelf savedShelf = shelfRepository.save(shelf);
        return new ShelfResponse(savedShelf.getId(), savedShelf.getName());
    }

    public ShelfResponse updateUserShelf(String username, Long shelfId, ShelfRequest request) {
        User user = userService.getCurrentUser(username);
        Shelf shelf = shelfRepository.findByIdAndUserId(shelfId, user.getId())
                .orElseThrow(() -> new ShelfNotFoundException("Shelf not found"));

        if (shelf.getType() != ShelfType.CUSTOM) {
            throw new IllegalArgumentException("Cannot update built-in shelf");
        }

        shelf.setName(request.getName());
        Shelf updated = shelfRepository.save(shelf);

        return new ShelfResponse(updated.getId(), updated.getName());
    }

    public void deleteUserShelf(String username, Long shelfId) {
        User user = userService.getCurrentUser(username);
        Shelf shelf = shelfRepository.findByIdAndUserId(shelfId, user.getId())
                .orElseThrow(() -> new ShelfNotFoundException("Shelf not found"));
        if (shelf.getType() != ShelfType.CUSTOM) {
            throw new IllegalArgumentException("Cannot delete built-in shelf");
        }
        shelfRepository.delete(shelf);
    }

    @Transactional
    public void addBookToShelf(String username, Long shelfId, Long bookId) {
        User user = userService.getCurrentUser(username);
        Shelf shelf = shelfRepository.findByIdAndUserId(shelfId, user.getId())
                .orElseThrow(() -> new ShelfNotFoundException("Shelf with id " + shelfId + " not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + bookId + " not found"));
        shelf.addBook(book);
        shelfRepository.save(shelf);
    }

    @Transactional
    public void removeBookFromShelf(String username, Long shelfId, Long bookId) {
        User user = userService.getCurrentUser(username);
        Shelf shelf = shelfRepository.findByIdAndUserId(shelfId, user.getId())
                .orElseThrow(() -> new ShelfNotFoundException("Shelf with id " + shelfId + " not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + bookId + " not found"));
        shelf.removeBook(book);
        shelfRepository.save(shelf);
    }

}
