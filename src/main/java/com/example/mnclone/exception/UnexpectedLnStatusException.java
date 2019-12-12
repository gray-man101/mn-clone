package com.example.mnclone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unexpected ln status")
public class UnexpectedLnStatusException extends RuntimeException {
}
