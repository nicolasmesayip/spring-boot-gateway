package com.nicolasmesa.springboot.shoppingcart.exception;

public class CartItemNotFound extends RuntimeException {
    public CartItemNotFound(String slug) {
        super("Cart Item not found with product slug: " + slug);
    }
}
