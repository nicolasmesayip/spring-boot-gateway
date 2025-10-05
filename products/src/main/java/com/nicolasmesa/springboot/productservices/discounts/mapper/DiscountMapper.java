package com.nicolasmesa.springboot.productservices.discounts.mapper;

import com.nicolasmesa.springboot.productservices.discounts.dto.DiscountDto;
import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    Discount toEntity(DiscountDto dto);

    DiscountDto toDto(Discount entity);

    List<DiscountDto> toDto(List<Discount> entity);
}
