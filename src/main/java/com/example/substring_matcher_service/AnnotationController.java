package com.example.substring_matcher_service;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.boot.jackson.JsonComponent;

@RestController
public class AnnotationController {
    private static final Logger logger = LogManager.getLogger(AnnotationController.class);

    @Autowired
    private DictionaryDAO dictionaryDao;

    @GetMapping("/annotate")
    @ResponseBody
    public ArrayList<int[]> annotate(
            @RequestParam @Required String target,
            @RequestParam @Required UUID dictId
            ) {
        logger.info(String.format("Using dictionary %s to annotate %s", dictId, target));

        try {
            ArrayList<int[]> result = new ArrayList<int[]>();
            Dictionary dict = dictionaryDao.getDictionaryById(dictId);
            int mask = dict.is_case_sensitive ? 0 : Pattern.CASE_INSENSITIVE;

            for (String entry : dict.entries) {
                Pattern p = Pattern.compile(entry, mask);
                Matcher m = p.matcher(target);
                while (m.find()) {
                    int[] indices = new int[2];
                    indices[0] = m.start();
                    indices[1] = m.end();
                    result.add(indices);
                }
            }

            return result;
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Dictionary not found"
                    );
        }
    }
}
