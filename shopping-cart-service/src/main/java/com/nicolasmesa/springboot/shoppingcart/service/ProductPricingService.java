package com.nicolasmesa.springboot.shoppingcart.service;

import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.shoppingcart.dto.ProductPricingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products-service", url = "${spring.services.products-service.url}")
public interface ProductPricingService {

    @GetMapping("/api/products/{slug}")
    ApiResponse<ProductPricingDto> getProductPricing(@PathVariable("slug") String slug);

}
