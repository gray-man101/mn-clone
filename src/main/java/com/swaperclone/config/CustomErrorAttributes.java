package com.swaperclone.config;

import com.swaperclone.common.exception.SwaperCloneException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * The aim of this class is to:
 * - provide a standard response to the API consumer in case of errors
 * - log errors that should be monitored
 */
@Component
@Slf4j
public class CustomErrorAttributes extends DefaultErrorAttributes {

    /**
     * Global error handling logic in Spring ecosystem. DTO validation exceptions are customized to standard
     * error response with concatenated messages of all its fields, database related exceptions are logged and
     * transformed to standard error response with constant message 'Cannot save data', all other exceptions are
     * logged and transformed to standard error response with constant message 'Cannot perform operation'.
     *
     * @param webRequest
     * @param includeStackTrace
     * @return map that is to be transformed to JSON response body
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Throwable e = getError(webRequest);
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        if (e instanceof SwaperCloneException) {
            return errorAttributes;
        } else if (e instanceof MethodArgumentNotValidException) {
            errorAttributes.put("message", ((MethodArgumentNotValidException) e).getBindingResult()
                    .getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
        } else if (e instanceof TransactionSystemException
                || e instanceof DataIntegrityViolationException
                || e instanceof ConstraintViolationException) {
            log.error("Cannot save data", e);
            errorAttributes.put("message", "Cannot save data");
        } else {
            log.error("Unexpected error", e);
            errorAttributes.put("message", "Cannot perform operation");
        }
        return errorAttributes;
    }
}
