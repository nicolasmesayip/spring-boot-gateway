package com.nicolasmesa.springboot.shoppingcart;

import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;
import com.nicolasmesa.springboot.shoppingcart.dto.ProductPricingDto;
import com.nicolasmesa.springboot.shoppingcart.entity.Cart;
import com.nicolasmesa.springboot.shoppingcart.entity.CartItem;
import com.nicolasmesa.springboot.shoppingcart.enums.CartStatus;
import com.nicolasmesa.springboot.testcommon.Generators;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.util.List;

public class CartGenerator extends Generators {
    @Provide
    Arbitrary<CartItemDto> genCartItemDto() {
        return Combinators.combine(genSlug, genInteger).as(CartItemDto::new);
    }

    @Provide
    Arbitrary<CartDto> genCartDto() {
        Arbitrary<List<CartItemDto>> cartItems = genCartItemDto().list().ofMinSize(1).ofMaxSize(5);

        return Combinators.combine(genEmailAddress, cartItems, genCurrency).as(CartDto::new);
    }

    @Provide
    Arbitrary<Cart> genCart() {
        return genCartDto().map(dto -> {
            Cart cart = new Cart();
            List<CartItem> cartItems = dto.items().stream().map(itemDto -> {
                CartItem item = new CartItem();
                item.setQuantity(itemDto.quantity());
                item.setProductSlug(itemDto.productSlug());
                item.setCart(cart);
                item.setUnitPrice(genPositiveBigDecimal.sample());
                return item;
            }).toList();

            cart.setCurrency(dto.currency());
            cart.setStatus(CartStatus.ACTIVE);
            cart.setUserId(dto.userId());
            cart.setItems(cartItems);

            return cart;
        });
    }

    @Provide
    Arbitrary<ProductPricingDto> genProductPricingDto() {
        return Combinators.combine(genSlug, genPositiveBigDecimal).as(ProductPricingDto::new);
    }
}
