package com.nicolasmesa.springboot.productservices.products.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nicolasmesa.springboot.common.model.Currency;
import com.nicolasmesa.springboot.common.validator.ValidSlug;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

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

        @Digits(integer = 13, fraction = 2)
        @DecimalMin(value = "0.00", inclusive = true, message = "Price cannot be negative.")
        @JsonSerialize(using = ToStringSerializer.class)
        BigDecimal price,

        @NotNull(message = "Currency is required")
        Currency currency,

        @NotNull(message = "Stock Available is required")
        @Min(value = 0, message = "Stock Available cannot be negative.")
        Integer stockAvailable,

        @NotNull(message = "Is Available is required")
        Boolean isAvailable
) {
}
