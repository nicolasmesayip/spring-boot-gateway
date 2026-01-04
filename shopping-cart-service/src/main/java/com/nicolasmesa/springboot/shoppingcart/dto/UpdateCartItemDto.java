package com.nicolasmesa.springboot.shoppingcart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateCartItemDto(
        @NotNull(message = "Quantity is required.")
        @Positive(message = "Quantity must be greater than 0.")
        Integer quantity
) {
}
