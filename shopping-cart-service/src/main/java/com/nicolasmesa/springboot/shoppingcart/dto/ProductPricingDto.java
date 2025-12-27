package com.nicolasmesa.springboot.shoppingcart.dto;

import java.math.BigDecimal;

public record ProductPricingDto(
        String slug,
        BigDecimal price
) {
}
