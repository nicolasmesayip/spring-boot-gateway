package com.nicolasmesa.springboot.productservices.products.dto;

import com.nicolasmesa.springboot.common.model.Currency;
import com.nicolasmesa.springboot.common.validator.ValidSlug;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductDto(
        @NotBlank(message = "Product name is required")
        @Size(max = 50, message = "Product name must be between 1 - 50 characters")
        String name,

        @ValidSlug
        @Size(max = 50, message = "Product slug must contain less than 50 characters")
        String slug,

        @NotBlank(message = "Product description is required")
        @Size(max = 255, message = "Product description must be between 1 - 255 characters")
        String description,

        @NotBlank(message = "Product category is required")
        @Size(max = 50, message = "Product category must be between 1 - 50 characters")
        String categorySlug,

        @NotNull
        @Min(value = 0, message = "Price cannot be negative.")
        Double price,

        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull(message = "Stock Available is required")
        @Min(value = 0, message = "Stock Available cannot be negative.")
        Integer stockAvailable,

        @NotNull(message = "Is Available is required")
        Boolean isAvailable
) {
}
