package com.reliaquest.api.employee.controller;

import com.reliaquest.api.employee.exception.MockServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class EmployeeControllerAdvice {

    Logger logger = LoggerFactory.getLogger(EmployeeControllerAdvice.class);

    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<String> handleTooManyRequests(HttpClientErrorException.TooManyRequests ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Mock server unable to process request");
    }

    @ExceptionHandler(MockServerException.class)
    protected ResponseEntity<?> handleException(MockServerException ex) {
        logger.error("Error handling web request.", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler
    protected ResponseEntity<?> handleException(Throwable ex) {
        logger.error("Error handling web request.", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
