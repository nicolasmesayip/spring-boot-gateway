package com.nicolasmesa.springboot.productservices.discounts.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products-service", url = "${spring.services.products-service.url}")
public interface ProductValidation {

    @GetMapping("/api/products/{slug}/exists")
    boolean existsBySlug(@PathVariable("slug") String slug);
}
