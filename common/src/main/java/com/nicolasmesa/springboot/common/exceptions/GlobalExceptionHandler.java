package com.nicolasmesa.springboot.common.exceptions;

import com.nicolasmesa.springboot.common.ResponseMethods;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
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

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingRequest(MissingRequestHeaderException ex) {
        return ResponseMethods.badRequest(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleEnumParseException(HttpMessageNotReadableException ex) {
        return ResponseMethods.badRequest("Invalid value provided for enum field: " + ex.getMessage());
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<?> handleConnectionException(ConnectException ex) {
        return ResponseMethods.serviceUnavailable("Service Unavailable: " + ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignStatus(FeignException ex) {
        return ResponseEntity.status(ex.status()).body(ex.contentUTF8());
    }
}
