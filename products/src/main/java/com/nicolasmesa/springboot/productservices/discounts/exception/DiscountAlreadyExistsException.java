package com.nicolasmesa.springboot.productservices.discounts.exception;

public class DiscountAlreadyExistsException extends RuntimeException {
    public DiscountAlreadyExistsException(String discountCode) {
        super("Discount code '" + discountCode + "' already exists");
    }
}
