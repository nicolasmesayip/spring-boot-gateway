package com.nicolasmesa.springboot.shoppingcart.exception;

public class CartNotFound extends RuntimeException {
    public CartNotFound(String userId) {
        super("Cart not found for user: " + userId);
    }
}
