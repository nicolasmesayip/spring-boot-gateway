package com.nicolasmesa.springboot.authentication.controller;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.dto.LoginCredentialsDto;
import com.nicolasmesa.springboot.authentication.mapper.EmailVerificationMapper;
import com.nicolasmesa.springboot.authentication.service.UserAuthenticationService;
import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
public class UserAuthenticationController {
    private final UserAuthenticationService userAuthenticationService;
    private final EmailVerificationMapper emailVerificationMapper;

    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
        this.emailVerificationMapper = EmailVerificationMapper.INSTANCE;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @NotNull LoginCredentialsDto credentials) {
        return ResponseMethods.ok(userAuthenticationService.login(credentials));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @NotNull LoginCredentialsDto credentials) {
        userAuthenticationService.register(credentials);
        return ResponseMethods.noContent();
    }

    @PostMapping(path = "/resetPassword")
    public ResponseEntity<ApiResponse<AuthResponse>> resetPassword(@RequestBody @NotNull LoginCredentialsDto credentials) {
        return ResponseMethods.ok(userAuthenticationService.resetPassword(credentials.email()));
    }

    @PostMapping(path = "/verifyOTPCode")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOTPCode(@RequestBody @NotNull EmailVerificationDto emailVerificationDto) {
        return ResponseMethods.ok(userAuthenticationService.verifyOTPCode(emailVerificationMapper.toEntity(emailVerificationDto)));
    }

    @PostMapping(path = "/updatePassword")
    public ResponseEntity<ApiResponse<AuthResponse>> updatePassword(@RequestBody @NotNull LoginCredentialsDto credentials) {
        userAuthenticationService.updatePasswordRequest(credentials);
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<AuthResponse>> deleteAccount(@PathVariable String email) {
        userAuthenticationService.deleteAccount(email);
        return ResponseMethods.noContent();
    }
}
