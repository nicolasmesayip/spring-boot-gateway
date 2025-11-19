package com.nicolasmesa.springboot.productservices.categories.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.productservices.categories.dto.CategoryDto;
import com.nicolasmesa.springboot.productservices.categories.dto.UpdateCategoryDescriptionDto;
import com.nicolasmesa.springboot.productservices.categories.mapper.CategoryMapper;
import com.nicolasmesa.springboot.productservices.categories.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

    @GetMapping(path = "/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> getCategoryBySlug(@NotNull @PathVariable String slug) {
        return ResponseMethods.ok(categoryMapper.toDto(categoryService.getCategoryBySlug(slug)));
    }

    @PostMapping(path = "/")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto category) {
        CategoryDto dto = categoryMapper.toDto(categoryService.createCategory(categoryMapper.toEntity(category)));
        return ResponseMethods.created(dto);
    }

    @PatchMapping(path = "/description/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateDescription(@NotNull @PathVariable String slug, @Valid @RequestBody UpdateCategoryDescriptionDto dto) {
        return ResponseMethods.ok(categoryMapper.toDto(categoryService.updateDescription(slug, dto.description())));
    }

    @PatchMapping("/activate/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> activate(@NotNull @PathVariable String slug) {
        return ResponseMethods.ok(categoryMapper.toDto(categoryService.changeStatus(slug, true)));
    }

    @PatchMapping("/deactivate/{slug}")
    public ResponseEntity<ApiResponse<CategoryDto>> deactivate(@NotNull @PathVariable String slug) {
        return ResponseMethods.ok(categoryMapper.toDto(categoryService.changeStatus(slug, false)));
    }
}
