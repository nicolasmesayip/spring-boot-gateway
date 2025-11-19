package com.nicolasmesa.springboot.productservices.products.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productName) {
        super("Product not found with name: " + productName);
    }
}
