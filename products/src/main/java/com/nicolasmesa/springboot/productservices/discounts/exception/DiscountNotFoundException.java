package com.nicolasmesa.springboot.productservices.discounts.exception;

public class DiscountNotFoundException extends RuntimeException {
    public DiscountNotFoundException(String discountCode) {
        super("Discount code '" + discountCode + "' was not found");
    }
}
