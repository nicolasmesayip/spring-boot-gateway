package com.nicolasmesa.springboot.productservices.categories.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String categoryName) {
        super("Category already exists: " + categoryName);
    }
}
