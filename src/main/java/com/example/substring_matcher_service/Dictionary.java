package com.example.substring_matcher_service;

import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.springframework.boot.jackson.JsonComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

@JsonComponent
public class Dictionary {
    public UUID id;
    public boolean is_case_sensitive;
    public List<String> entries;

    public Dictionary() {
        this(UUID.randomUUID(), new ArrayList<String>(), false);
    }

    public Dictionary(String id, String entries, boolean is_case_sensitive) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        this.id = UUID.fromString(id);
        this.entries = mapper.readValue(entries, new TypeReference<ArrayList<String>>() { });
        this.is_case_sensitive = is_case_sensitive;
    }

    public Dictionary(UUID id, List<String> entries, boolean is_case_sensitive) {
        this.id = id;
        this.is_case_sensitive = is_case_sensitive;
        this.entries = entries;
    }

    public Dictionary(List<String> entries, boolean is_case_sensitive) {
        this(UUID.randomUUID(), entries, is_case_sensitive);
    }
}
