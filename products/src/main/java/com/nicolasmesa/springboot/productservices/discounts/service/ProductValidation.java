package com.nicolasmesa.springboot.productservices.discounts.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products", url = "http://localhost:8082")
public interface ProductValidation {

    @GetMapping("/api/products/{slug}/exists")
    boolean existsBySlug(@PathVariable("slug") String slug);
}
