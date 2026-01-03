package com.example.goodreads.service.admin;

import com.example.goodreads.dao.AuthorDao;
import com.example.goodreads.dto.admin.author.AuthorDto;
import com.example.goodreads.exception.AuthorNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAuthorService {

    private final AuthorDao authorDao;

    public AdminAuthorService(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public List<AuthorDto> getAllAuthors() {
        return authorDao.findAll();
    }

    public AuthorDto getAuthorById(Long id) {
        return authorDao.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id " + id + " not found"));
    }

    public void createAuthor(AuthorDto authorDto) {
        authorDao.save(authorDto);
    }

    public void updateAuthor(Long id, AuthorDto authorDto) {
        authorDao.update(id, authorDto);
    }

    public void deleteAuthor(Long id) {
        authorDao.deleteById(id);
    }
}
