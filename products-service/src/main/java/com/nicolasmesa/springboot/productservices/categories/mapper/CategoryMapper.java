package com.nicolasmesa.springboot.productservices.categories.mapper;

import com.nicolasmesa.springboot.productservices.categories.dto.CategoryDto;
import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryDto dto);

    CategoryDto toDto(Category entity);

    List<CategoryDto> toDto(List<Category> entity);
}

