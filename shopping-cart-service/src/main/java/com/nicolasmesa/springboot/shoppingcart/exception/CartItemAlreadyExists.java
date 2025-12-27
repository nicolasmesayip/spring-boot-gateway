package com.nicolasmesa.springboot.shoppingcart.exception;

public class CartItemAlreadyExists extends RuntimeException {
    public CartItemAlreadyExists(String slug) {
        super("Cart Item already exists with product slug: " + slug);
    }
}
