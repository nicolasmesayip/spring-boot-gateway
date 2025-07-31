package com.nicolasmesa.springboot.products.controller;

import com.nicolasmesa.springboot.products.config.ResponseMethods;
import com.nicolasmesa.springboot.products.config.model.ApiResponse;
import com.nicolasmesa.springboot.products.dto.ProductDTO;
import com.nicolasmesa.springboot.products.entity.Product;
import com.nicolasmesa.springboot.products.mapper.ProductMapper;
import com.nicolasmesa.springboot.products.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/products")
@Validated
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseMethods.ok(ProductMapper.convertToDTOList(products));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable long id) {
        return productRepository.findById(id)
                .map(p -> ResponseMethods.ok(ProductMapper.convertToDTO(p)))
                .orElse(ResponseMethods.notFound(List.of("Product not found")));
    }

    @PostMapping(path = "/")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO product) {
        productRepository.save(ProductMapper.convertToEntity(product));
        return ResponseMethods.ok(product);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable long id, @Valid @RequestBody ProductDTO productRequest) {
        if (!productRepository.existsById(id)) {
            return ResponseMethods.notFound(List.of("Product not found"));
        }
        Product product = ProductMapper.convertToEntity(productRequest);
        product.setId(id);
        productRepository.save(product);
        return ResponseMethods.ok(productRequest);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable long id) {
        if (!productRepository.existsById(id)) {
            return ResponseMethods.notFound(List.of("Product not found"));
        }
        productRepository.deleteById(id);
        return ResponseMethods.noContent();
    }

    @GetMapping(path = "/with-stock")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsWithStock() {
        List<Product> products = productRepository.findProductsWithStockAvailable();
        return ResponseMethods.ok(ProductMapper.convertToDTOList(products));
    }

    @GetMapping(path = "/without-stock")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsWithoutStock() {
        List<Product> products = productRepository.findProductsWithoutStockAvailable();
        return ResponseMethods.ok(ProductMapper.convertToDTOList(products));
    }

    @GetMapping(path = "/available")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAvailableProducts() {
        List<Product> products = productRepository.findAvailableProducts();
        return ResponseMethods.ok(ProductMapper.convertToDTOList(products));
    }

    @GetMapping(path = "/unavailable")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getUnavailableProducts() {
        List<Product> products = productRepository.findUnavailableProducts();
        return ResponseMethods.ok(ProductMapper.convertToDTOList(products));
    }

    @GetMapping(path = "/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productRepository.findProductsByCategory(category);
        return ResponseMethods.ok(ProductMapper.convertToDTOList(products));
    }

    @GetMapping(path = "/name/{name}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductByName(@PathVariable String name) {
        return productRepository.findProductByName(name)
                .map(p -> ResponseMethods.ok(ProductMapper.convertToDTO(p)))
                .orElse(ResponseMethods.notFound(List.of("Product not found")));
    }
}
