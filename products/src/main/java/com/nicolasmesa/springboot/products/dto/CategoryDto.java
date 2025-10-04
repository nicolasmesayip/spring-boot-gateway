package com.nicolasmesa.springboot.products.dto;

import com.nicolasmesa.springboot.common.validator.ValidSlug;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        @NotBlank(message = "The Category name is required")
        @Size(max = 50, message = "The Category name must be between 1 and 50 characters.")
        String name,

        @ValidSlug
        @Size(max = 50, message = "The Category slug must be between 1 and 50 characters.")
        String slug,

        @NotBlank(message = "The Category description is required")
        @Size(max = 255, message = "The Category description must be between 1 and 255 characters.")
        String description,

        @NotNull(message = "The Category status is required.")
        Boolean isActive
) {
}
