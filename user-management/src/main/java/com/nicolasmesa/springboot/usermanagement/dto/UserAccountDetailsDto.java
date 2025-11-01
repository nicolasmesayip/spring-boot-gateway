package com.nicolasmesa.springboot.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record UserAccountDetailsDto(

        @NotBlank(message = "First Name is required.")
        @Size(min = 1, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @NotBlank(message = "Last Name is required.")
        @Size(min = 1, max = 100, message = "Last name must be between 2 and 100 characters")
        String lastName,

        @NotBlank(message = "Email Address is required.")
        @Size(min = 1, max = 100, message = "Email address must be between 1 and 100 characters")
        String emailAddress,

        @NotBlank(message = "Mobile Number is required.")
        @Pattern(regexp = "^\\d{4,14}$", message = "Mobile number must contain only digits and be 4 to 14 digits")
        String mobileNumber,

        @NotBlank(message = "Country Code is required.")
        @Pattern(regexp = "^[1-9]\\d{1,3}$", message = "Country code must be a valid dialing code, e.g. +1 or 44")
        String countryCode,

        @NotBlank(message = "Home Address is required.")
        @Size(min = 1, max = 255, message = "Home address must be between 1 and 255 characters")
        String homeAddress,

        @Past(message = "Birth date must be in the past")
        Date dateOfBirth
) {
}
