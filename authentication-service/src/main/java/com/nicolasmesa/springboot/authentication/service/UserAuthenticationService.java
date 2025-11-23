package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;

public interface UserAuthenticationService {
    AuthResponse login(UserCredentialsDto credentials);

    AuthResponse resetPassword(String email);

    AuthResponse verifyOTPCode(EmailVerification emailDetails);

    void updatePasswordRequest(UserCredentialsDto credentials);
}
