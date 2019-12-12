package com.example.mnclone.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MnExceptionHandler {

    //TODO predotvratitj keisi, napisatj javadoc, chto eto dlja krajnih sluchaev
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(new HashMap<String, String>() {{
            put("message", "cannot perform operation");
        }}, HttpStatus.BAD_REQUEST);
    }

}
