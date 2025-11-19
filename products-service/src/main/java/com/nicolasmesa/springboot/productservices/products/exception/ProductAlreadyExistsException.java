package com.nicolasmesa.springboot.productservices.products.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String productName) {
        super("Product already exists with product name: " + productName);
    }
}
