package com.nicolasmesa.springboot.shoppingcart.repository;

import com.nicolasmesa.springboot.shoppingcart.entity.Cart;
import com.nicolasmesa.springboot.shoppingcart.enums.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(String userId);

    Optional<Cart> findByUserIdAndStatus(String userId, CartStatus status);
}
