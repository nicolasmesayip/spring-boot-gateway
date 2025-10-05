package com.nicolasmesa.springboot.products.exception;

public class DiscountAlreadyExistsException extends RuntimeException {
    public DiscountAlreadyExistsException(String discountCode) {
        super("Discount code '" + discountCode + "' already exists");
    }
}
