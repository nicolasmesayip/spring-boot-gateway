package com.nicolasmesa.springboot.productservices.categories.service;

import com.nicolasmesa.springboot.productservices.categories.entity.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);

    Category updateDescription(String slug, String newDescription);

    Category changeStatus(String slug, Boolean newStatus);

    List<Category> getAllCategories();

    Category getCategoryBySlug(String slug);
}
