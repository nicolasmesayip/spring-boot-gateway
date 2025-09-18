package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.dto.LoginCredentialsDto;
import org.springframework.http.ResponseEntity;

public interface UserAuthenticationService {
    ResponseEntity<AuthResponse> login(LoginCredentialsDto credentials);

    ResponseEntity<AuthResponse> register(LoginCredentialsDto credentials);

    ResponseEntity<AuthResponse> resetPassword(String email);

    ResponseEntity<AuthResponse> verifyOTPCode(EmailVerificationDto emailDetails);

    ResponseEntity<AuthResponse> updatePasswordRequest(LoginCredentialsDto credentials);

    ResponseEntity<AuthResponse> deleteAccount(String email);
}
