package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Product;

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

    List<Product> getProductsByCategoryName(String categoryName);
}
