package com.nicolasmesa.springboot.authentication.controller;

import com.nicolasmesa.springboot.authentication.entity.AuthResponse;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.LoginCredentials;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.authentication.service.UserAuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/auth")
public class UserAuthenticationController {
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;

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
        return userAuthenticationService.resetPassword(credentials.getEmail());
    }

    @PostMapping(path = "/verifyOTPCode")
    public ResponseEntity<AuthResponse> verifyOTPCode(@RequestBody @NotNull EmailVerification emailVerification) {
        return userAuthenticationService.verifyOTPCode(emailVerification);
    }

    @PostMapping(path = "/updatePassword")
    public ResponseEntity<AuthResponse> updatePassword(@RequestBody @NotNull LoginCredentials credentials) {
        return userAuthenticationService.updatePasswordRequest(credentials);
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<AuthResponse> getByEmail(@PathVariable String email) {
        userAuthenticationRepository.findById(email);
        return ResponseEntity.ok(new AuthResponse(null, "Test"));
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<AuthResponse> deleteAccount(@PathVariable String email) {
        return userAuthenticationService.deleteAccount(email);
    }
}
