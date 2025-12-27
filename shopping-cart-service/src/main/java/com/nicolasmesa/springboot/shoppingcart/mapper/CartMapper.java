package com.nicolasmesa.springboot.shoppingcart.mapper;

import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.shoppingcart.dto.CartItemDto;
import com.nicolasmesa.springboot.shoppingcart.entity.Cart;
import com.nicolasmesa.springboot.shoppingcart.entity.CartItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toEntity(CartDto dto);

    CartItem toEntity(CartItemDto dto);

    CartDto toDto(Cart entity);

    List<CartDto> toDto(List<Cart> entity);
}
