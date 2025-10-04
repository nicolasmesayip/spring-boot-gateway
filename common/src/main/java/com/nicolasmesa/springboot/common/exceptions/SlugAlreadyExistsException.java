package com.nicolasmesa.springboot.common.exceptions;

public class SlugAlreadyExistsException extends RuntimeException {
    public SlugAlreadyExistsException(String slug) {
        super("Slug '" + slug + "' already exists.");
    }
}
