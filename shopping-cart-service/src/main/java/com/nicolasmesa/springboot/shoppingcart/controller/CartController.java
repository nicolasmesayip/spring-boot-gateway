package com.nicolasmesa.springboot.shoppingcart.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;
import com.nicolasmesa.springboot.shoppingcart.dto.UpdateCartItemDto;
import com.nicolasmesa.springboot.shoppingcart.service.CartService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/cart")
@Validated
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress) {
        return ResponseMethods.ok(cartService.getCart(emailAddress));
    }

    @PostMapping(path = "/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress, @NotNull @RequestBody CartItemDto itemDto) {
        return ResponseMethods.created(cartService.addItem(emailAddress, itemDto));
    }

    @DeleteMapping(path = "/items/{productSlug}")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress, @NotNull @PathVariable String productSlug) {
        cartService.removeItem(emailAddress, productSlug);
        return ResponseMethods.noContent();
    }

    @PutMapping(path = "/items/{productSlug}")
    public ResponseEntity<ApiResponse<CartDto>> updateItem(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress, @NotNull @PathVariable String productSlug, @NotNull @RequestBody UpdateCartItemDto quantityDto) {
        CartItemDto itemDto = new CartItemDto(productSlug, quantityDto.quantity());
        cartService.updateItem(emailAddress, itemDto);
        return ResponseMethods.noContent();
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<CartDto>> clearCart(@RequestHeader("X-GATEWAY-EMAIL") String emailAddress) {
        cartService.clearCart(emailAddress);
        return ResponseMethods.noContent();
    }
}
