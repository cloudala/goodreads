package com.example.goodreads.dao.mapper;

import com.example.goodreads.dto.admin.author.AuthorDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorRowMapper implements RowMapper<AuthorDto> {

    @Override
    public AuthorDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AuthorDto(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
