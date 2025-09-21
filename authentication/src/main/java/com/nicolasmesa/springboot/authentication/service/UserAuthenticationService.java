package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.LoginCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;

public interface UserAuthenticationService {
    AuthResponse login(LoginCredentialsDto credentials);

    void register(LoginCredentialsDto credentials);

    AuthResponse resetPassword(String email);

    AuthResponse verifyOTPCode(EmailVerification emailDetails);

    void updatePasswordRequest(LoginCredentialsDto credentials);

    void deleteAccount(String email);
}
