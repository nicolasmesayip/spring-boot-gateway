package com.nicolasmesa.springboot.authentication.dto;

public record EmailVerificationDto(String email, int verificationOtpCode) {
}
