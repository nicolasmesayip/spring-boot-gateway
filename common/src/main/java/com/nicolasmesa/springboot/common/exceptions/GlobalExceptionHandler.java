package com.nicolasmesa.springboot.common.exceptions;

import com.nicolasmesa.springboot.common.ResponseMethods;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<?> handleUserNotFound(UnAuthorizedException ex) {
        return ResponseMethods.unAuthorized(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleUserNotFound(IllegalArgumentException ex) {
        return ResponseMethods.badRequest(ex.getMessage());
    }

    @ExceptionHandler(UnExpectedException.class)
    public ResponseEntity<?> handleUnexpectedException(UnExpectedException ex) {
        return ResponseMethods.forbidden(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        return ResponseMethods.badRequest(errors);
    }
}
