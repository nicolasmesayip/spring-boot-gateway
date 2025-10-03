package com.nicolasmesa.springboot.products.utils;

import com.nicolasmesa.springboot.products.entity.Discount;

import java.time.LocalDateTime;

public class DiscountUtils {

    public static boolean isDiscountActive(Discount discount) {
        final LocalDateTime now = LocalDateTime.now();
        return discount.getIsActive() && (discount.getStartDateTime().isAfter(now) && discount.getEndDateTime().isBefore(now));
    }

    public static boolean isDiscountCodeValid(Discount discount) {
        return isDiscountActive(discount) && discount.getTimesUsed() < discount.getMaxUses();
    }
}
