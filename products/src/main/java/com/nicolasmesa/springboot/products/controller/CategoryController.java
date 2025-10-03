package com.nicolasmesa.springboot.products.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.products.dto.CategoryDto;
import com.nicolasmesa.springboot.products.mapper.CategoryMapper;
import com.nicolasmesa.springboot.products.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/categories")
@Validated
public class CategoryController {
    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    public CategoryController(CategoryMapper categoryMapper, CategoryService categoryService) {
        this.categoryMapper = categoryMapper;
        this.categoryService = categoryService;
    }

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        return ResponseMethods.ok(categoryMapper.toDto(categoryService.getAllCategories()));
    }

    @PostMapping(path = "/")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto category) {
        CategoryDto dto = categoryMapper.toDto(categoryService.createCategory(categoryMapper.toEntity(category)));
        return ResponseMethods.created(dto);
    }
}
