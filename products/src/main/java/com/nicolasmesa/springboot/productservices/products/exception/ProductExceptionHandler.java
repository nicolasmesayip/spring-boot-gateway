package com.nicolasmesa.springboot.productservices.products.exception;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.exceptions.GlobalExceptionHandler;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(ProductNotFoundException ex) {
        return ResponseMethods.notFound(String.valueOf(ex.getMessage()));
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExists(ProductAlreadyExistsException ex) {
        return ResponseMethods.conflict(String.valueOf(ex.getMessage()));
    }
}
