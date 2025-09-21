package com.nicolasmesa.springboot.authentication.exception;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.exceptions.GlobalExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class UserExceptionHandler extends GlobalExceptionHandler {
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<?> handleForbidden(AccountLockedException ex) {
        return ResponseMethods.forbidden(ex.getMessage());
    }
}
