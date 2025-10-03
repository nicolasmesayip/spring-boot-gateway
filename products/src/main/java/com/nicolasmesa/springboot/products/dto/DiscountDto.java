package com.nicolasmesa.springboot.products.dto;

import com.nicolasmesa.springboot.products.entity.DiscountTypes;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record DiscountDto(

        @NotBlank(message = "Discount code is required")
        @Size(max = 50, message = "Discount code must be between 1 - 50 characters")
        String discountCode,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be between 1 - 255 characters")
        String description,

        @NotNull(message = "Discount Type is required")
        DiscountTypes discountType,

        @NotNull(message = "Discount quantity/percentage is required")
        @Positive(message = "Discount quantity/percentage must be a positive value")
        Double discount,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency must be 3 characters")
        String currency,

        @Positive(message = "Minimum Purchase Amount must be a positive value")
        Double minimumPurchaseAmount,

        @NotNull(message = "IsActive is required")
        Boolean isActive,

        @Positive(message = "Maximum Uses must be a positive value")
        Integer maxUses,

        @NotNull(message = "Is Stackable is required")
        Boolean isStackable,

        @NotNull(message = "Start Date Time is required")
        @FutureOrPresent(message = "The Start Date Time must be in Present or Future")
        LocalDateTime startDateTime,

        @Future(message = "The End Date Time must be in the Future")
        LocalDateTime endDateTime
) {
}
