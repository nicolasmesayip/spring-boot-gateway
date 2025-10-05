package com.nicolasmesa.springboot.products.exception;

public class DiscountNotFoundException extends RuntimeException {
    public DiscountNotFoundException(String discountCode) {
        super("Discount code '" + discountCode + "' was not found");
    }
}
