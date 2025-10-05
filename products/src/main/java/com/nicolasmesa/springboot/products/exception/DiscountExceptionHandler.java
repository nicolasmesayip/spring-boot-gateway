package com.nicolasmesa.springboot.products.exception;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.exceptions.GlobalExceptionHandler;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DiscountExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(DiscountAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleDiscountAlreadyExists(DiscountAlreadyExistsException ex) {
        return ResponseMethods.conflict(String.valueOf(ex.getMessage()));
    }

    @ExceptionHandler(DiscountNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleDiscountNotFound(DiscountNotFoundException ex) {
        return ResponseMethods.badRequest(String.valueOf(ex.getMessage()));
    }
}
