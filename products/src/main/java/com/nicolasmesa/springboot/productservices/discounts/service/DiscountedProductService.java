package com.nicolasmesa.springboot.productservices.discounts.service;

import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;

import java.util.List;

public interface DiscountedProductService {
    List<DiscountedProduct> addDiscountToProducts(String discountId, List<String> productIds);

    void removeDiscountToProducts(String discountId, List<String> productIds);
}
