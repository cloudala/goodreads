package com.example.goodreads.controller.rest.user;

import com.example.goodreads.dto.shelf.ShelfDetailsResponse;
import com.example.goodreads.dto.shelf.ShelfRequest;
import com.example.goodreads.dto.shelf.ShelfResponse;
import com.example.goodreads.service.user.ShelfService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/shelves")
@PreAuthorize("isAuthenticated()")
public class ShelfController {
    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @GetMapping
    public ResponseEntity<List<ShelfResponse>> getUserShelves(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(shelfService.getUserShelves(username));
    }

    @GetMapping("/{shelfId}")
    public ResponseEntity<ShelfDetailsResponse> getShelfById(Authentication authentication, @PathVariable Long shelfId) {
        String username = authentication.getName();
        ShelfDetailsResponse shelfDetailsResponse = shelfService.getShelfById(username, shelfId);
        return ResponseEntity.ok(shelfDetailsResponse);
    }

    @PostMapping
    public ResponseEntity<ShelfResponse> addShelfToUser(Authentication authentication, @RequestBody ShelfRequest shelfRequest) {
        String username = authentication.getName();
        ShelfResponse shelfResponse = shelfService.addShelfToUser(username, shelfRequest);
        return ResponseEntity.created(URI.create("/api/shelves")).body(shelfResponse);
    }

    @PutMapping("/{shelfId}")
    public ResponseEntity<ShelfResponse> updateShelf(
            Authentication authentication,
            @PathVariable Long shelfId,
            @RequestBody ShelfRequest request) {

        String username = authentication.getName();
        return ResponseEntity.ok(shelfService.updateUserShelf(username, shelfId, request));
    }

    @DeleteMapping("/{shelfId}")
    public ResponseEntity<Void> deleteShelf(Authentication authentication, @PathVariable Long shelfId) {
        String username = authentication.getName();
        shelfService.deleteUserShelf(username, shelfId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<Void> addBookToShelf(
            Authentication authentication,
            @PathVariable Long shelfId,
            @PathVariable Long bookId) {
        String username = authentication.getName();
        shelfService.addBookToShelf(username, shelfId, bookId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{shelfId}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromShelf(
            Authentication authentication,
            @PathVariable Long shelfId,
            @PathVariable Long bookId) {
        String username = authentication.getName();
        shelfService.removeBookFromShelf(username, shelfId, bookId);
        return ResponseEntity.noContent().build();
    }

}
