package com.nicolasmesa.springboot.productservices.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryDescriptionDto(
        @NotBlank(message = "The Category description is required")
        @Size(max = 255, message = "The Category description must be between 1 and 255 characters.")
        String description
) {
}
