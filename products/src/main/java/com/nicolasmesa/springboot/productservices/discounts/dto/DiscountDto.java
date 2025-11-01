package com.nicolasmesa.springboot.productservices.discounts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.nicolasmesa.springboot.common.model.Currency;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountTypes;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.nicolasmesa.springboot.common.Constants.DATE_TIME_FORMAT;

public record DiscountDto(

        @NotBlank(message = "Discount code is required")
        @Pattern(regexp = "^[A-Z0-9]{6,12}$", message = "Discount code must be 6-12 characters, uppercase letters and digits only")
        String discountCode,

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be between 1 - 255 characters")
        String description,

        @NotNull(message = "Discount Type is required")
        DiscountTypes discountType,

        @Digits(integer = 13, fraction = 2)
        @DecimalMin(value = "0.00", inclusive = true, message = "Discount quantity/percentage must be a positive value")
        @JsonSerialize(using = ToStringSerializer.class)
        BigDecimal discount,

        Currency currency,

        @Digits(integer = 13, fraction = 2)
        @DecimalMin(value = "0.00", inclusive = true, message = "Minimum Purchase Amount must be a positive value")
        @JsonSerialize(using = ToStringSerializer.class)
        BigDecimal minimumPurchaseAmount,

        @NotNull(message = "IsActive is required")
        Boolean isActive,

        @Positive(message = "Maximum Uses must be a positive value")
        Integer maxUses,

        @NotNull(message = "Is Stackable is required")
        Boolean isStackable,

        @NotNull(message = "Start Date Time is required")
        @FutureOrPresent(message = "The Start Date Time must be in Present or Future")
        @JsonFormat(pattern = DATE_TIME_FORMAT)
        LocalDateTime startDateTime,

        @Future(message = "The End Date Time must be in the Future")
        @JsonFormat(pattern = DATE_TIME_FORMAT)
        LocalDateTime endDateTime,

        @NotNull(message = "Update User is required")
        @Size(max = 50, message = "Update User must be between 1 and 50 characters")
        String updatedBy,

        @Size(max = 50, message = "Creation User must be between 1 and 50 characters")
        String createdBy
) {
}
