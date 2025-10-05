package com.nicolasmesa.springboot.products.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.products.dto.DiscountDto;
import com.nicolasmesa.springboot.products.mapper.DiscountMapper;
import com.nicolasmesa.springboot.products.service.DiscountService;
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
    private final DiscountMapper discountMapper;

    public DiscountController(DiscountService discountService, DiscountMapper discountMapper) {
        this.discountService = discountService;
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
    public ResponseEntity<ApiResponse<DiscountDto>> createDiscount(@Valid @NotNull @RequestBody DiscountDto discount) {
        return ResponseMethods.created(discountMapper.toDto(discountService.createDiscount(discountMapper.toEntity(discount))));
    }

    @DeleteMapping(path = "/{discountCode}")
    public ResponseEntity<ApiResponse<Void>> deleteDiscount(@NotNull @PathVariable String discountCode) {
        discountService.deleteDiscount(discountCode);
        return ResponseMethods.noContent();
    }
}
