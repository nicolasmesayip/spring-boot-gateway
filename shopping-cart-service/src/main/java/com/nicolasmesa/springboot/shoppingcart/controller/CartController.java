package com.nicolasmesa.springboot.shoppingcart.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;
import com.nicolasmesa.springboot.shoppingcart.service.CartService;
import com.nicolasmesa.springboot.shoppingcart.service.CartServiceImpl;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/cart")
@Validated
public class CartController {
    private final CartService cartService;

    public CartController(CartServiceImpl cartService) {
        this.cartService = cartService;
    }

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<CartDto>> getCart(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress) {
        return ResponseMethods.ok(cartService.getCart(emailAddress));
    }

    @PostMapping(path = "/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress, @NotNull @RequestBody CartItemDto itemDto) {
        return ResponseMethods.created(cartService.addItem(emailAddress, itemDto));
    }

    @DeleteMapping(path = "/items")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress, @NotNull @RequestBody CartItemDto itemDto) {
        cartService.removeItem(emailAddress, itemDto);
        return ResponseMethods.noContent();
    }

    @PutMapping(path = "/items")
    public ResponseEntity<ApiResponse<CartDto>> updateItem(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress, @NotNull @RequestBody CartItemDto itemDto) {
        cartService.updateItem(emailAddress, itemDto);
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/clear")
    public ResponseEntity<ApiResponse<CartDto>> clearCart(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress) {
        cartService.clearCart(emailAddress);
        return ResponseMethods.noContent();
    }
}
