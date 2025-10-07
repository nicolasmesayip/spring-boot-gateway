package com.nicolasmesa.springboot.productservices.discounts.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.productservices.discounts.dto.DiscountDto;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;
import com.nicolasmesa.springboot.productservices.discounts.mapper.DiscountMapper;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountService;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountedProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/discounts")
@Validated
public class DiscountController {
    private final DiscountService discountService;
    private final DiscountedProductService discountedProductService;
    private final DiscountMapper discountMapper;

    public DiscountController(DiscountService discountService, DiscountedProductService discountedProductService, DiscountMapper discountMapper) {
        this.discountService = discountService;
        this.discountedProductService = discountedProductService;
        this.discountMapper = discountMapper;
    }

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<List<DiscountDto>>> getAllDiscounts() {
        return ResponseMethods.ok(discountMapper.toDto(discountService.getAllDiscounts()));
    }

    @GetMapping(path = "/{discountCode}")
    public ResponseEntity<ApiResponse<DiscountDto>> getByDiscountCode(@NotNull @PathVariable String discountCode) {
        return ResponseMethods.ok(discountMapper.toDto(discountService.getByDiscountCode(discountCode)));
    }

    @PostMapping(path = "/")
    public ResponseEntity<ApiResponse<DiscountDto>> createDiscount(@Valid @NotNull @RequestBody DiscountDto discountDto) {
        return ResponseMethods.created(discountMapper.toDto(discountService.createDiscount(discountMapper.toEntity(discountDto))));
    }

    @DeleteMapping(path = "/{discountCode}")
    public ResponseEntity<ApiResponse<Void>> deleteDiscount(@NotNull @PathVariable String discountCode) {
        discountService.deleteDiscount(discountCode);
        return ResponseMethods.noContent();
    }

    @PostMapping(path = "/{discountCode}/add-products")
    public ResponseEntity<ApiResponse<List<String>>> addDiscountToProducts(@NotNull @PathVariable String discountCode, @Valid @RequestBody List<String> productSlugs) {
        List<String> validSlugs = discountedProductService.addDiscountToProducts(discountCode, productSlugs).stream().map(DiscountedProduct::getProductSlug).toList();
        return ResponseMethods.created(validSlugs);
    }

    @PostMapping(path = "/{discountCode}/remove-products")
    public ResponseEntity<ApiResponse<List<String>>> removeDiscountToProducts(@NotNull @PathVariable String discountCode, @Valid @RequestBody List<String> productSlugs) {
        discountedProductService.removeDiscountToProducts(discountCode, productSlugs);
        return ResponseMethods.noContent();
    }

    @PostMapping(path = "/{discountCode}/products/{productSlug}")
    public ResponseEntity<ApiResponse<List<String>>> addDiscountToProduct(@NotNull @PathVariable String discountCode, @NotNull @PathVariable String productSlug) {
        List<String> validSlugs = discountedProductService.addDiscountToProducts(discountCode, List.of(productSlug)).stream().map(DiscountedProduct::getProductSlug).toList();
        return ResponseMethods.created(validSlugs);
    }

    @DeleteMapping(path = "/{discountCode}/products/{productSlug}")
    public ResponseEntity<ApiResponse<String>> removeDiscountToProduct(@NotNull @PathVariable String discountCode, @NotNull @PathVariable String productSlug) {
        discountedProductService.removeDiscountToProducts(discountCode, List.of(productSlug));
        return ResponseMethods.noContent();
    }

}
