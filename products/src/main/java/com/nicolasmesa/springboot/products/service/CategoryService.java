package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);

    Category updateDescription(String slug, String newDescription);

    Category changeStatus(String slug, Boolean newStatus);

    List<Category> getAllCategories();

    Category getCategoryBySlug(String slug);
}
