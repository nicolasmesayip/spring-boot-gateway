package com.nicolasmesa.springboot.authentication.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException() {
        super("Account locked due to too many failed login attempts. Please contact support or try again later.");
    }
}
