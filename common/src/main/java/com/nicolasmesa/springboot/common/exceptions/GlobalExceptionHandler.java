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
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseMethods.badRequest(ex.getMessage());
    }

    @ExceptionHandler(InvalidSlugException.class)
    public ResponseEntity<?> handleInvalidSlug(InvalidSlugException ex) {
        return ResponseMethods.badRequest(ex.getMessage());
    }

    @ExceptionHandler(SlugAlreadyExistsException.class)
    public ResponseEntity<?> handleInvalidSlug(SlugAlreadyExistsException ex) {
        return ResponseMethods.conflict(ex.getMessage());
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
