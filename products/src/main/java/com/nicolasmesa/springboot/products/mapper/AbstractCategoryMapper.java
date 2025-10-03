package com.nicolasmesa.springboot.products.mapper;

import com.nicolasmesa.springboot.products.entity.Category;
import com.nicolasmesa.springboot.products.repository.CategoryRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AbstractCategoryMapper {
    @Autowired
    protected CategoryRepository categoryRepository;

    public Category fromCategoryName(String value) {
        if (value == null) return null;
        return categoryRepository.findByName(value)
                .orElseThrow(() -> new IllegalArgumentException("Unknown category: " + value));
    }

    public String toCategoryName(Category category) {
        return category != null ? category.getName() : null;
    }
}
