package com.example.goodreads.dao;

import com.example.goodreads.dao.mapper.AuthorRowMapper;
import com.example.goodreads.dto.admin.author.AuthorDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AuthorDao {

    private final JdbcTemplate jdbcTemplate;

    public AuthorDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ===== READ =====

    public List<AuthorDto> findAll() {
        String sql = "SELECT id, name FROM authors";
        return jdbcTemplate.query(sql, new AuthorRowMapper());
    }

    public Optional<AuthorDto> findById(Long id) {
        String sql = "SELECT id, name FROM authors WHERE id = ?";
        return jdbcTemplate.query(sql, new AuthorRowMapper(), id)
                .stream()
                .findFirst();
    }

    // ===== CREATE =====

    public void save(AuthorDto authorDto) {
        String sql = "INSERT INTO authors (name) VALUES (?)";
        jdbcTemplate.update(sql, authorDto.getName());
    }

    // ===== UPDATE =====

    public void update(Long id, AuthorDto authorDto) {
        String sql = "UPDATE authors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, authorDto.getName(), id);
    }

    // ===== DELETE =====

    public void deleteById(Long id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
