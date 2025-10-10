package com.nicolasmesa.springboot.productservices.categories.service;

import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.categories.exception.CategoryNotFoundException;
import com.nicolasmesa.springboot.productservices.categories.repository.CategoryRepository;
import com.nicolasmesa.springboot.productservices.common.service.SlugService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final SlugService slugService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, SlugService slugService) {
        this.categoryRepository = categoryRepository;
        this.slugService = slugService;
    }

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.findByName(category.getName()).isPresent())
            throw new CategoryAlreadyExistsException(category.getName());

        category.setSlug(slugService.verifySlug(category.getName(), category.getSlug(), categoryRepository));

        return categoryRepository.save(category);
    }

    @Override
    public Category updateDescription(String slug, String newDescription) {
        Category category = categoryRepository.findBySlug(slug).orElseThrow(() -> new CategoryNotFoundException(slug));
        category.setDescription(newDescription);
        return categoryRepository.save(category);
    }

    @Override
    public Category changeStatus(String slug, Boolean newStatus) {
        Category category = categoryRepository.findBySlug(slug).orElseThrow(() -> new CategoryNotFoundException(slug));
        category.setIsActive(newStatus);
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug).orElseThrow(() -> new CategoryNotFoundException(slug));
    }
}
