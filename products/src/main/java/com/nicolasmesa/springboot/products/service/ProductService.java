package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product getProductByName(String productName);

    Product createProduct(Product product);

    void deleteProduct(Long id);

    void updateProduct(Long id, Product product);

    List<Product> getProductsWithStock();

    List<Product> getProductsWithoutStock();

    List<Product> getAvailableProducts();

    List<Product> getUnavailableProducts();

    List<Product> getProductsByCategoryName(String categoryName);
}
