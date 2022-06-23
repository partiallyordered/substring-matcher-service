package com.example.substring_matcher_service;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public class DictionaryRowMapper implements RowMapper<Dictionary> {
    @Override
    public Dictionary mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        try {
            return new Dictionary(
                rs.getString("id"),
                rs.getString("entries"),
                rs.getBoolean("is_case_sensitive")
            );
        } catch (JsonProcessingException e) {
            throw new EntriesJsonDeserializeException("Couldn't deserialize entries JSON string from DB");
        }
    }
}
