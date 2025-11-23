package com.nicolasmesa.springboot.authentication.dto;

import com.nicolasmesa.springboot.common.dto.UserAccountDetailsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record UserRegisterRequest(
        @Valid
        @NotNull(message = "User Credentials are required.")
        UserCredentialsDto userCredentials,

        @Valid
        @NotNull(message = "User Account Details are required")
        UserAccountDetailsDto userAccountDetails
) {
}
