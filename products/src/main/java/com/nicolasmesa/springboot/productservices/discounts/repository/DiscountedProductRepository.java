package com.nicolasmesa.springboot.productservices.discounts.repository;

import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountedProductRepository extends JpaRepository<DiscountedProduct, Long> {
    Boolean existsByDiscountAndProductSlug(Discount discount, String productSlug);

    List<DiscountedProduct> findAllByDiscountAndProductSlugIn(Discount discount, List<String> productSlugs);
}
