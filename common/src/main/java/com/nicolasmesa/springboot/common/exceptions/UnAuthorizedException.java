package com.nicolasmesa.springboot.common.exceptions;

public class UnAuthorizedException extends RuntimeException {
    public UnAuthorizedException() {
        super("You are not authorized to perform the action.");
    }
}
