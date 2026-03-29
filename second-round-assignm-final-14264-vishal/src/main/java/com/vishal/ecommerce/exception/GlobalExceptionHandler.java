package com.vishal.ecommerce.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

        @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("message", "Something went wrong");
        error.put("timestamp", LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    
    }
    @ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {

Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.NOT_FOUND.value());
    error.put("message", ex.getMessage());
    error.put("timestamp", LocalDateTime.now());

    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
}
@ExceptionHandler(BadRequestException.class)
public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {

Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.BAD_REQUEST.value());
    error.put("message", ex.getMessage());
    error.put("timestamp", LocalDateTime.now());

    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
}
@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {

    Map<String, Object> error = new HashMap<>();
    error.put("status", HttpStatus.UNAUTHORIZED.value());
    error.put("message", ex.getMessage());
    error.put("timestamp", LocalDateTime.now());

    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
}
}
