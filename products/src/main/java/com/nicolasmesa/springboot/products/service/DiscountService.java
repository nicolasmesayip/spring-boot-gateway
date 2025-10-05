package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Discount;

import java.util.List;

public interface DiscountService {
    List<Discount> getAllDiscounts();

    Discount getByDiscountCode(String discountCode);

    Discount createDiscount(Discount discount);

    void deleteDiscount(String discountCode);
}
