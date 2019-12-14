package com.example.mnclone.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Throwable error = getError(webRequest);
        log.debug("Unexpected error", error);
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        if (error instanceof MethodArgumentNotValidException) {
            errorAttributes.put("message", ((MethodArgumentNotValidException) error).getBindingResult()
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
        }
        return errorAttributes;
    }
}
