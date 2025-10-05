package com.nicolasmesa.springboot.productservices.discounts.service;

import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;

import java.util.List;

public interface DiscountService {
    List<Discount> getAllDiscounts();

    Discount getByDiscountCode(String discountCode);

    Discount createDiscount(Discount discount);

    void deleteDiscount(String discountCode);
}
