package com.nicolasmesa.springboot.common.exceptions;

public class InvalidSlugException extends RuntimeException {
    public InvalidSlugException(String slug) {
        super("Slug '" + slug + "' is not valid. Use only lowercase letters, numbers, and hyphens.");
    }
}
