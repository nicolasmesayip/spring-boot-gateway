package com.nicolasmesa.springboot.shoppingcart.dto;

import com.nicolasmesa.springboot.common.validator.ValidSlug;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CartItemDto(
        @ValidSlug
        @Size(max = 50, message = "The Product slug must be between 1 and 50 characters.")
        String productSlug,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be greater than 0")
        Integer quantity
) {
}
