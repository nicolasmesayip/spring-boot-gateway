package com.nicolasmesa.springboot.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailVerificationDto(
        @NotBlank(message = "Email Address is required.")
        @Size(min = 1, max = 100, message = "Email address must be between 1 and 100 characters")
        @Email(message = "Invalid email format")
        String emailAddress,

        int verificationOtpCode
) {
}
