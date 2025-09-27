package com.nicolasmesa.springboot.authentication.exception;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.exceptions.UserManagementExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler extends UserManagementExceptionHandler {
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<?> handleForbidden(AccountLockedException ex) {
        return ResponseMethods.forbidden(ex.getMessage());
    }
}
