package com.nicolasmesa.springboot.shoppingcart.service;

import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;

public interface CartService {
    CartDto getCart(String emailAddress);

    CartDto addItem(String emailAddress, CartItemDto itemDto);

    void removeItem(String emailAddress, String productSlug);

    void updateItem(String emailAddress, CartItemDto itemDto);

    void clearCart(String emailAddress);
}
