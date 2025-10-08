package com.nicolasmesa.springboot.productservices.discounts.service;

import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;

import java.util.List;

public interface DiscountedProductService {
    List<DiscountedProduct> addDiscountToProducts(String discountCode, List<String> productSlugs);

    List<DiscountedProduct> getDiscountedProducts(String discountCode);

    void removeDiscountToProducts(String discountId, List<String> productIds);
}
