package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.entity.AuthResponse;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.LoginCredentials;
import org.springframework.http.ResponseEntity;

public interface UserAuthenticationService {
    ResponseEntity<AuthResponse> login(LoginCredentials credentials);

    ResponseEntity<AuthResponse> register(LoginCredentials credentials);

    ResponseEntity<AuthResponse> resetPassword(String email);

    ResponseEntity<AuthResponse> verifyOTPCode(EmailVerification emailDetails);

    ResponseEntity<AuthResponse> updatePasswordRequest(LoginCredentials credentials);

    ResponseEntity<AuthResponse> deleteAccount(String email);
}
