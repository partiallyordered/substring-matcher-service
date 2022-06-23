package com.example.substring_matcher_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Repository // TODO: Repository? Something else? Is this concept overcomplicated for this purpose?
public class DictionaryDAO {
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private static final Logger logger = LogManager.getLogger(DictionaryController.class);

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private static String entriesToJsonStr(List<String> entries) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(entries);
    }

    public void createDictionary(Dictionary dict) throws JsonProcessingException {
        logger.info(String.format("Creating dictionary %s", dict));
        String entriesJsonStr = DictionaryDAO.entriesToJsonStr(dict.entries);
        jdbcTemplate.update(
            "INSERT INTO dictionary (id, entries, is_case_sensitive) VALUES (?, ?, ?)",
            dict.id, entriesJsonStr, dict.is_case_sensitive);
    }

    public Dictionary getDictionaryById(UUID id) {
        logger.info(String.format("Retrieving dictionary %s", id));
        return jdbcTemplate.queryForObject(
            "SELECT * FROM dictionary WHERE id = ?",
            new DictionaryRowMapper(),
            id.toString());
    }

    public void updateDictionaryEntries(UUID id, List<String> entries) throws JsonProcessingException {
        String entriesJsonStr = DictionaryDAO.entriesToJsonStr(entries);
        logger.info(String.format("Setting dictionary %s entries to %s", id, entriesJsonStr));
        jdbcTemplate.update("UPDATE dictionary SET entries = ? WHERE id = ?", entriesJsonStr, id);
    }

    public void updateDictionaryIsCaseSensitive(UUID id, boolean is_case_sensitive) {
        logger.info(
            String.format("Setting dictionary %s is_case_sensitive to %s", id, is_case_sensitive));
        jdbcTemplate.update(
            "UPDATE dictionary SET is_case_sensitive = ? WHERE id = ?",
            is_case_sensitive,
            id);
    }

    public void upsertDictionary(Dictionary dict) throws JsonProcessingException {
        String entriesJsonStr = DictionaryDAO.entriesToJsonStr(dict.entries);
        logger.info(String.format(
            "Upserting dictionary %s. Setting is_case_sensitive to %s; entries to %s",
            dict.id,
            dict.is_case_sensitive,
            entriesJsonStr));
        jdbcTemplate.update(
            // Non-standard SQL (fine for the purposes of this exercise):
            """
            INSERT INTO dictionary (id, entries, is_case_sensitive) VALUES (?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
              entries=?,
              is_case_sensitive=?
            """,
            // INSERT args
            dict.id, entriesJsonStr, dict.is_case_sensitive,
            // ON CONFLICT args
            entriesJsonStr, dict.is_case_sensitive);
    }

    public void deleteDictionaryById(UUID id) {
        logger.info(String.format("Deleting dictionary %s", id));
        jdbcTemplate.update("DELETE FROM dictionary WHERE id = ?", id);
    }

    public List<Dictionary> listDictionaries() {
        logger.info("Retrieving dictionary list");
        return jdbcTemplate.query("SELECT * FROM dictionary", new DictionaryRowMapper());
    }
}
