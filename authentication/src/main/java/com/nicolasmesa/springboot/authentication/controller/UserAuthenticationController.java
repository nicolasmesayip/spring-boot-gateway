package com.nicolasmesa.springboot.authentication.controller;

import com.nicolasmesa.springboot.authentication.entity.AuthResponse;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.LoginCredentials;
import com.nicolasmesa.springboot.authentication.service.UserAuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/api/auth")
public class UserAuthenticationController {
    private final UserAuthenticationService userAuthenticationService;

    public UserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @NotNull LoginCredentials credentials) {
        return userAuthenticationService.login(credentials);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @NotNull LoginCredentials credentials) {
        return userAuthenticationService.register(credentials);
    }

    @PostMapping(path = "/resetPassword")
    public ResponseEntity<AuthResponse> resetPassword(@RequestBody @NotNull LoginCredentials credentials) {
        return userAuthenticationService.resetPassword(credentials.email());
    }

    @PostMapping(path = "/verifyOTPCode")
    public ResponseEntity<AuthResponse> verifyOTPCode(@RequestBody @NotNull EmailVerification emailVerification) {
        return userAuthenticationService.verifyOTPCode(emailVerification);
    }

    @PostMapping(path = "/updatePassword")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody @NotNull LoginCredentials credentials) {
        return userAuthenticationService.updatePasswordRequest(credentials);
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<AuthResponse> deleteAccount(@PathVariable String email) {
        return userAuthenticationService.deleteAccount(email);
    }
}
