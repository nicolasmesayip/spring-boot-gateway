package com.nicolasmesa.springboot.productservices.categories.exception;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.exceptions.GlobalExceptionHandler;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CategoryExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return ResponseMethods.badRequest(String.valueOf(ex.getMessage()));
    }
}
