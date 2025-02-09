package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.entity.EmailVerification;

public interface EmailVerificationService {
    void sendEmail(String emailRecipient);

    boolean isOTPCodeValid(EmailVerification emailVerificationDetails);

    void deleteValidatedOTPCode(EmailVerification emailVerificationDetails);
}
