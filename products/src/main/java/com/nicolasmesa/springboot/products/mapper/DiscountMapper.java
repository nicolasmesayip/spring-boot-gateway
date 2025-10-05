package com.nicolasmesa.springboot.products.mapper;

import com.nicolasmesa.springboot.products.dto.DiscountDto;
import com.nicolasmesa.springboot.products.entity.Discount;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DiscountMapper {
    Discount toEntity(DiscountDto dto);

    DiscountDto toDto(Discount entity);

    List<DiscountDto> toDto(List<Discount> entity);
}
