package com.nicolasmesa.springboot.productservices.products.mapper;

import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryNotFoundException;
import com.nicolasmesa.springboot.productservices.categories.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AbstractCategoryMapper {
    @Autowired
    protected CategoryRepository categoryRepository;

    public Category fromCategoryName(String value) {
        if (value == null) return null;
        return categoryRepository.findByName(value)
                .orElse(categoryRepository.findBySlug(value)
                        .orElseThrow(() -> new CategoryNotFoundException(value)));
    }

    public String toCategoryName(Category category) {
        return category != null ? category.getName() : null;
    }
}
