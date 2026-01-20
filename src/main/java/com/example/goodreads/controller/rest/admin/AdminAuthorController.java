package com.example.goodreads.controller.rest.admin;

import com.example.goodreads.dto.admin.author.AuthorDto;
import com.example.goodreads.service.admin.AdminAuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/authors")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuthorController {

    private final AdminAuthorService adminAuthorService;

    public AdminAuthorController(AdminAuthorService adminAuthorService) {
        this.adminAuthorService = adminAuthorService;
    }

    @GetMapping
    public ResponseEntity<List<AuthorDto>> getAll() {
        return ResponseEntity.ok(adminAuthorService.getAllAuthors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adminAuthorService.getAuthorById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody AuthorDto authorDto) {
        adminAuthorService.createAuthor(authorDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody AuthorDto authorDto) {
        adminAuthorService.updateAuthor(id, authorDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminAuthorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
