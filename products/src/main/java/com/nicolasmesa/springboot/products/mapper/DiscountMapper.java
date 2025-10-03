package com.nicolasmesa.springboot.products.mapper;

import com.nicolasmesa.springboot.products.dto.DiscountDto;
import com.nicolasmesa.springboot.products.entity.Discount;
import org.mapstruct.Mapper;

@Mapper
public interface DiscountMapper {
    Discount toEntity(DiscountDto dto);

    DiscountDto toDto(Discount entity);
}
