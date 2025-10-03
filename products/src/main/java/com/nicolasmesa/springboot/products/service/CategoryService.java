package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);

    List<Category> getAllCategories();
}
