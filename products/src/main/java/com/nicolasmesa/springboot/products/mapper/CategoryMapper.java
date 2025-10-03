package com.nicolasmesa.springboot.products.mapper;

import com.nicolasmesa.springboot.products.dto.CategoryDto;
import com.nicolasmesa.springboot.products.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryDto dto);

    CategoryDto toDto(Category entity);

    List<CategoryDto> toDto(List<Category> entity);
}

