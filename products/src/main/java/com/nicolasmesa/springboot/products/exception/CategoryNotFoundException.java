package com.nicolasmesa.springboot.products.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String category) {
        super("Category not found with name: " + category);
    }

}
