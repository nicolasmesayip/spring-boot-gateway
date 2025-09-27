package com.nicolasmesa.springboot.usermanagement.exception;

import com.nicolasmesa.springboot.common.exceptions.UserManagementExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionHandler extends UserManagementExceptionHandler {
}
