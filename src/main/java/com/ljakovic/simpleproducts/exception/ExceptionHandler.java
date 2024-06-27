package com.ljakovic.simpleproducts.exception;

import com.ljakovic.simpleproducts.client.exception.HnbException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    /**
     * Handles exceptions of type HnbException
     * Logs the error message and returns a response entity with a 500 Internal Server Error status and the error message in the body
     *
     * @param e The HnbException to handle
     * @return A ResponseEntity with a 500 Internal Server Error status and the error message
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({HnbException.class})
    public ResponseEntity<Object> handleHnbException(HnbException e) {
        LOG.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(e.getMessage());
    }

    /**
     * Handles exceptions of type MethodArgumentNotValidException
     * Logs the error message and returns a response entity with a 400 Bad Request status and a list of field-specific error messages in the body
     *
     * @param e The MethodArgumentNotValidException to handle
     * @return A ResponseEntity with a 400 Bad Request status and a list of field-specific error messages
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOG.error(e.getMessage(), e);
        final List<String> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + " - " + errorMessage);
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    /**
     * Handles exceptions of type DataIntegrityViolationException
     * Logs the error message and returns a response entity with a 400 Bad Request status and the error message in the body
     *
     * @param e The DataIntegrityViolationException to handle
     * @return A ResponseEntity with a 400 Bad Request status and the error message
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({NonUniqueProductCodeException.class})
    public ResponseEntity<Object> handleNonUniqueProductCodeException(NonUniqueProductCodeException e) {
        LOG.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }

    /**
     * Handles exceptions of type EntityNotFoundException
     * Logs the error message and returns a response entity with a 404 Not Found status and the error message in the body
     *
     * @param e The EntityNotFoundException to handle
     * @return A ResponseEntity with a 404 Not Found status and the error message
     */
    @org.springframework.web.bind.annotation.ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        LOG.error(e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }
}
