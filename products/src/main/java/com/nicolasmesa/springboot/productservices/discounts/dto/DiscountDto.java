package com.nicolasmesa.springboot.productservices.discounts.dto;

import com.nicolasmesa.springboot.common.model.Currency;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountTypes;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record DiscountDto(

        @NotBlank(message = "Discount code is required")
        @Pattern(regexp = "^[A-Z0-9]{6,12}$", message = "Discount code must be 6-20 characters, uppercase letters and digits only")
        String discountCode,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be between 1 - 255 characters")
        String description,

        @NotNull(message = "Discount Type is required")
        DiscountTypes discountType,

        @NotNull(message = "Discount quantity/percentage is required")
        @Positive(message = "Discount quantity/percentage must be a positive value")
        Double discount,

        @NotNull(message = "Currency is required")
        Currency currency,

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
        LocalDateTime endDateTime,

        @NotNull(message = "Update User is required")
        @Size(max = 50, message = "Update User must be between 1 and 50 characters")
        String updatedBy,

        @Size(max = 50, message = "Creation User must be between 1 and 50 characters")
        String createdBy
) {
}
