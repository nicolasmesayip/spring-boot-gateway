package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Category;
import com.nicolasmesa.springboot.products.exception.ProductAlreadyExistsException;
import com.nicolasmesa.springboot.products.repository.CategoryRepository;
import com.nicolasmesa.springboot.products.service.utils.SlugService;
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
            throw new ProductAlreadyExistsException(category.getName());

        category.setSlug(slugService.verifySlug(category.getName(), category.getSlug(), categoryRepository));

        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
