package com.nicolasmesa.springboot.common.exceptions;

public class UnExpectedException extends RuntimeException {
    public UnExpectedException(String message) {
        super(message);
    }
}
