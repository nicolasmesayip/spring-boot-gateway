package com.nicolasmesa.springboot.shoppingcart.exception;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.exceptions.GlobalExceptionHandler;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CartExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(CartNotFound.class)
    public ResponseEntity<ApiResponse<Object>> handleCartNotFound(CartNotFound ex) {
        return ResponseMethods.notFound(String.valueOf(ex.getMessage()));
    }

    @ExceptionHandler(CartItemNotFound.class)
    public ResponseEntity<ApiResponse<Object>> handleCartItemNotFound(CartItemNotFound ex) {
        return ResponseMethods.notFound(String.valueOf(ex.getMessage()));
    }

    @ExceptionHandler(CartItemAlreadyExists.class)
    public ResponseEntity<ApiResponse<Object>> handleCartItemAlreadyExists(CartItemAlreadyExists ex) {
        return ResponseMethods.conflict(String.valueOf(ex.getMessage()));
    }
}
