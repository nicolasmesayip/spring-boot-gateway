package com.nicolasmesa.springboot.shoppingcart.dto;

import com.nicolasmesa.springboot.common.model.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CartDto(
        @NotNull(message = "Cart Id is required")
        Long id,

        @NotBlank(message = "User email address is required")
        @Size(max = 100, message = "Email address must be between 1 - 100 characters")
        String userId,

        List<CartItemDto> items,

        @NotNull(message = "Currency is required")
        Currency currency
) {
}
