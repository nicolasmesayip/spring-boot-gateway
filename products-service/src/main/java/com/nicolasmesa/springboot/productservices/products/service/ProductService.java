package com.nicolasmesa.springboot.productservices.products.service;

import com.nicolasmesa.springboot.productservices.products.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductBySlug(String slug);

    Product createProduct(Product product);

    void deleteProduct(String slug);

    void updateProduct(String slug, Product product);

    List<Product> getProductsWithStock();

    List<Product> getProductsWithoutStock();

    List<Product> getAvailableProducts();

    List<Product> getUnavailableProducts();

    List<Product> getProductsByCategorySlug(String categoryName);

    Boolean existsBySlug(String slug);
}
