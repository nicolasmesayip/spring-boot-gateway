package com.nicolasmesa.springboot.productservices.products.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.productservices.products.dto.ProductDto;
import com.nicolasmesa.springboot.productservices.products.mapper.ProductMapper;
import com.nicolasmesa.springboot.productservices.products.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/products")
@Validated
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAllProducts() {
        return ResponseMethods.ok(productMapper.toDto(productService.getAllProducts()));
    }

    @PostMapping(path = "/")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductDto product) {
        ProductDto dto = productMapper.toDto(productService.createProduct(productMapper.toEntity(product)));
        return ResponseMethods.created(dto);
    }

    @PutMapping(path = "/{slug}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(@NotNull @PathVariable String slug, @Valid @RequestBody ProductDto productRequest) {
        productService.updateProduct(slug, productMapper.toEntity(productRequest));
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/{slug}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@NotNull @PathVariable String slug) {
        productService.deleteProduct(slug);
        return ResponseMethods.noContent();
    }

    @GetMapping(path = "/with-stock")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsWithStock() {
        return ResponseMethods.ok(productMapper.toDto(productService.getProductsWithStock()));
    }

    @GetMapping(path = "/without-stock")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsWithoutStock() {
        return ResponseMethods.ok(productMapper.toDto(productService.getProductsWithoutStock()));
    }

    @GetMapping(path = "/available")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getAvailableProducts() {
        return ResponseMethods.ok(productMapper.toDto(productService.getAvailableProducts()));
    }

    @GetMapping(path = "/unavailable")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getUnavailableProducts() {
        return ResponseMethods.ok(productMapper.toDto(productService.getUnavailableProducts()));
    }

    @GetMapping(path = "/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsByCategory(@NotNull @PathVariable String category) {
        return ResponseMethods.ok(productMapper.toDto(productService.getProductsByCategoryName(category)));
    }

    @GetMapping(path = "/{slug}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBySlug(@NotNull @PathVariable String slug) {
        return ResponseMethods.ok(productMapper.toDto(productService.getProductBySlug(slug)));
    }

    @GetMapping(path = "/{slug}/exists")
    public Boolean existsBySlug(@NotNull @PathVariable String slug) {
        return productService.existsBySlug(slug);
    }
}
