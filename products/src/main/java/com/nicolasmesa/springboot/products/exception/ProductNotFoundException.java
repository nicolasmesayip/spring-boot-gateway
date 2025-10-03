package com.nicolasmesa.springboot.products.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

    public ProductNotFoundException(String productName) {
        super("Product not found with name: " + productName);
    }
}
