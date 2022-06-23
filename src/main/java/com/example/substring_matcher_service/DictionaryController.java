package com.example.substring_matcher_service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class DictionaryController {
    private static final Logger logger = LogManager.getLogger(DictionaryController.class);
    @Autowired
    private DictionaryDAO dictionaryDao;

    @GetMapping("/dictionary")
    @ResponseBody
    public List<Dictionary> getDictionaries() {
        return dictionaryDao.listDictionaries();
    }

    @DeleteMapping("/dictionary/{id}")
    public void deleteDictionaryById(@PathVariable(value = "id") UUID id) {
        dictionaryDao.deleteDictionaryById(id);
    }

    @PutMapping("/dictionary/{id}")
    public ResponseEntity putDictionaryById(@RequestBody Dictionary dict) {
        try {
            dictionaryDao.upsertDictionary(dict);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (JsonProcessingException e) {
            // This should never happen, because the string was deserialized from JSON in the
            // first place
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't serialize entries to JSON"
                    );
        }
    }

    @PutMapping("/dictionary/{id}/is_case_sensitive")
    public void putDictionaryIsCaseSensitive(
            @PathVariable(value = "id") UUID id,
            @RequestBody boolean is_case_sensitive
            ) {
        dictionaryDao.updateDictionaryIsCaseSensitive(id, is_case_sensitive);
    }

    @PutMapping("/dictionary/{id}/entries")
    public void putDictionaryEntries(
            @PathVariable(value = "id") UUID id,
            @RequestBody ArrayList<String> entries
            ) {
        try {
            dictionaryDao.updateDictionaryEntries(id, entries);
        } catch (JsonProcessingException e) {
            // This should never happen, because the string was deserialized from JSON in the
            // first place
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't serialize entries to JSON"
                    );
        }
    }

    @PostMapping("/dictionary")
    public ResponseEntity postDictionary(@RequestBody Dictionary newDict) {
        try {
            dictionaryDao.createDictionary(newDict);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (JsonProcessingException e) {
            // This should never happen, because the string was deserialized from JSON in the
            // first place
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't serialize entries to JSON"
                    );
        }
    }

    @GetMapping("/dictionary/{id}")
    @ResponseBody
    public Dictionary getDictionaryById(@PathVariable(value = "id") UUID id) {
        try {
            return dictionaryDao.getDictionaryById(id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Dictionary not found"
                    );
        }
    }
}
