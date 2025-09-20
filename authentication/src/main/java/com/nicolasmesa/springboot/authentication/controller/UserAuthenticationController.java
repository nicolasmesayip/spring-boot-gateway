package com.nicolasmesa.springboot.authentication.controller;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.dto.LoginCredentialsDto;
import com.nicolasmesa.springboot.authentication.service.UserAuthenticationService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
public class UserAuthenticationController {
    private final UserAuthenticationService userAuthenticationService;

    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @NotNull LoginCredentialsDto credentials) {
        return userAuthenticationService.login(credentials);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @NotNull LoginCredentialsDto credentials) {
        return userAuthenticationService.register(credentials);
    }

    @PostMapping(path = "/resetPassword")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody @NotNull LoginCredentialsDto credentials) {
        return userAuthenticationService.resetPassword(credentials.email());
    }

    @PostMapping(path = "/verifyOTPCode")
    public ResponseEntity<AuthResponse> verifyOTPCode(@RequestBody @NotNull EmailVerificationDto emailVerificationDto) {
        return userAuthenticationService.verifyOTPCode(emailVerificationDto);
    }

    @PostMapping(path = "/updatePassword")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody @NotNull LoginCredentialsDto credentials) {
        return userAuthenticationService.updatePasswordRequest(credentials);
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<AuthResponse> deleteAccount(@PathVariable String email) {
        return userAuthenticationService.deleteAccount(email);
    }
}
