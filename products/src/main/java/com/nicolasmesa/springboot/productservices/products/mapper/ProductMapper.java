package com.nicolasmesa.springboot.productservices.products.mapper;

import com.nicolasmesa.springboot.productservices.products.dto.ProductDto;
import com.nicolasmesa.springboot.productservices.products.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = AbstractCategoryMapper.class)
public interface ProductMapper {
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product entity);

    List<ProductDto> toDto(List<Product> entity);
}
