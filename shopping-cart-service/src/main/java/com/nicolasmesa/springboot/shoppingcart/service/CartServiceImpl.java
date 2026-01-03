package com.nicolasmesa.springboot.shoppingcart.service;

import com.nicolasmesa.springboot.common.model.Currency;
import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;
import com.nicolasmesa.springboot.shoppingcart.dto.ProductPricingDto;
import com.nicolasmesa.springboot.shoppingcart.entity.Cart;
import com.nicolasmesa.springboot.shoppingcart.entity.CartItem;
import com.nicolasmesa.springboot.shoppingcart.enums.CartStatus;
import com.nicolasmesa.springboot.shoppingcart.exception.CartItemAlreadyExists;
import com.nicolasmesa.springboot.shoppingcart.exception.CartNotFound;
import com.nicolasmesa.springboot.shoppingcart.mapper.CartMapper;
import com.nicolasmesa.springboot.shoppingcart.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final ProductPricingService productPricingService;

    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper, ProductPricingService productPricingService) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.productPricingService = productPricingService;
    }

    @Override
    public CartDto getCart(String emailAddress) {
        Cart cart = cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE).orElseThrow(() -> new CartNotFound(emailAddress));
        return cartMapper.toDto(cart);
    }

    @Override
    public CartDto addItem(String emailAddress, CartItemDto itemDto) {
        CartItem item = cartMapper.toEntity(itemDto);
        Cart cart = cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE).orElse(createCart(emailAddress));

        cart.findItemByProductSlug(item).ifPresent(value -> {
            throw new CartItemAlreadyExists(item.getProductSlug());
        });

        ProductPricingDto productPricingDto = productPricingService.getProductPricing(itemDto.productSlug()).getData();

        item.setUnitPrice(productPricingDto.price());
        cart.addItem(item);

        return cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public void removeItem(String emailAddress, String productSlug) {
        Cart cart = cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE).orElseThrow(() -> new CartNotFound(emailAddress));

        cart.removeItem(productSlug);

        cartMapper.toDto(cartRepository.save(cart));
    }

    @Override
    public void updateItem(String emailAddress, CartItemDto itemDto) {
        CartItem item = cartMapper.toEntity(itemDto);
        Cart cart = cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE).orElseThrow(() -> new CartNotFound(emailAddress));

        cart.updateItem(item);

        cartMapper.toDto(cartRepository.save(cart));

    }

    @Override
    public void clearCart(String emailAddress) {
        Cart cart = cartRepository.findByUserIdAndStatus(emailAddress, CartStatus.ACTIVE).orElseThrow(() -> new CartNotFound(emailAddress));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Cart createCart(String emailAddress) {
        Cart cart = new Cart();

        cart.setStatus(CartStatus.ACTIVE);
        cart.setCurrency(Currency.GBP);
        cart.setItems(new ArrayList<>());
        cart.setUserId(emailAddress);

        return cart;
    }
}
