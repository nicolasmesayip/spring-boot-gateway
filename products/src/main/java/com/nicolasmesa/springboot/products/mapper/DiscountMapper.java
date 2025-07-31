package com.nicolasmesa.springboot.products.mapper;

import com.nicolasmesa.springboot.products.dto.DiscountDTO;
import com.nicolasmesa.springboot.products.entity.Discount;

public class DiscountMapper {

    public static Discount convertToEntity(DiscountDTO discountDTO) {
        return new Discount(
                discountDTO.getDiscountCode(),
                discountDTO.getDescription(),
                discountDTO.getDiscountType(),
                discountDTO.getDiscount(),
                discountDTO.getCurrency(),
                discountDTO.getMinimumPurchaseAmount(),
                discountDTO.isActive(),
                discountDTO.getTimesUsed(),
                discountDTO.getMaxUses(),
                discountDTO.isStackable(),
                discountDTO.getStartDateTime(),
                discountDTO.getEndDateTime()
        );
    }
}
