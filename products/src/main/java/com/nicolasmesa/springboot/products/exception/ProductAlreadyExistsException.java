package com.nicolasmesa.springboot.products.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String productName) {
        super("Product already exists with product name: " + productName);
    }

    public ProductAlreadyExistsException(Long id) {
        super("Product already exists with id: " + id);
    }
}
