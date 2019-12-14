package com.example.mnclone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class MnExceptionHandler extends ResponseEntityExceptionHandler {

    //TODO predotvratitj keisi, napisatj javadoc, chto eto dlja krajnih sluchaev
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Throwable t) {
        log.error("Unexpected error", t);
        return new ResponseEntity<>(new HashMap<String, String>() {{
            put("message", t.getMessage());
            put("messaget", t.getMessage() + "t");
        }}, HttpStatus.BAD_REQUEST);
    }

}
