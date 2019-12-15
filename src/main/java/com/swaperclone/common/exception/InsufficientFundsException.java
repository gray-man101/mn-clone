package com.swaperclone.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends SwaperCloneException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
