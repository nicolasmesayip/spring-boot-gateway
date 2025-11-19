package com.nicolasmesa.springboot.productservices.discounts.service;

import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountNotFoundException;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountRepository;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountedProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscountedProductServiceImpl implements DiscountedProductService {
    private final DiscountedProductRepository discountedProductRepository;
    private final DiscountRepository discountRepository;
    private final ProductValidation productValidation;

    public DiscountedProductServiceImpl(DiscountedProductRepository discountedProductRepository, DiscountRepository discountRepository, ProductValidation productValidation) {
        this.discountedProductRepository = discountedProductRepository;
        this.discountRepository = discountRepository;
        this.productValidation = productValidation;
    }

    @Override
    public List<DiscountedProduct> addDiscountToProducts(String discountCode, List<String> productSlugs) {
        Discount discount = discountRepository.findByDiscountCode(discountCode).orElseThrow(() -> new DiscountNotFoundException(discountCode));

        List<String> validProductSlugs = productSlugs.stream().filter(productValidation::existsBySlug).toList();
        validProductSlugs = validProductSlugs.stream().filter(d -> !discountedProductRepository.existsByDiscountAndProductSlug(discount, d)).toList();

        List<DiscountedProduct> discountedProducts = validProductSlugs.stream().map(p -> new DiscountedProduct(discount, p)).toList();
        return discountedProductRepository.saveAll(discountedProducts);
    }

    @Override
    public List<DiscountedProduct> getDiscountedProducts(String discountCode) {
        Discount discount = discountRepository.findByDiscountCode(discountCode).orElseThrow(() -> new DiscountNotFoundException(discountCode));

        return discountedProductRepository.findAllByDiscount(discount);
    }

    @Override
    public void removeDiscountToProducts(String discountCode, List<String> productSlugs) {
        Discount discount = discountRepository.findByDiscountCode(discountCode).orElseThrow(() -> new DiscountNotFoundException(discountCode));

        List<String> validProductSlugs = productSlugs.stream().filter(productValidation::existsBySlug).toList();
        List<DiscountedProduct> discountedProducts = discountedProductRepository.findAllByDiscountAndProductSlugIn(discount, validProductSlugs);

        if (discountedProducts.isEmpty()) {
            return;
        }

        discountedProductRepository.deleteAll(discountedProducts);
    }
}
