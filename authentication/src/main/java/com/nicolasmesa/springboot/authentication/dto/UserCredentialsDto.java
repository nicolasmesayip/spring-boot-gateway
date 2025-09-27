package com.nicolasmesa.springboot.authentication.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCredentialsDto(
        @NotBlank(message = "Email Address is required.")
        @Size(min = 1, max = 100, message = "Email address must be between 1 and 100 characters")
        String emailAddress,

        @NotBlank(message = "Password is required.")
        @Size(min = 1, max = 50, message = "Password must be between 1 and 50 characters")
        String password
) {
}
