package com.nicolasmesa.springboot.products.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.products.dto.ProductDto;
import com.nicolasmesa.springboot.products.mapper.ProductMapper;
import com.nicolasmesa.springboot.products.service.ProductService;
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

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@NotNull @PathVariable Long id) {
        return ResponseMethods.ok(productMapper.toDto(productService.getProductById(id)));
    }

    @PostMapping(path = "/")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductDto product) {
        ProductDto dto = productMapper.toDto(productService.createProduct(productMapper.toEntity(product)));
        return ResponseMethods.created(dto);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<Void>> updateProduct(@NotNull @PathVariable Long id, @Valid @RequestBody ProductDto productRequest) {
        productService.updateProduct(id, productMapper.toEntity(productRequest));
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
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

    @GetMapping(path = "/name/{name}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductByName(@NotNull @PathVariable String name) {
        return ResponseMethods.ok(productMapper.toDto(productService.getProductByName(name)));
    }
}
