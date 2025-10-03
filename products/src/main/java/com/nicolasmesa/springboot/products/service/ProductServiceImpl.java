package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Product;
import com.nicolasmesa.springboot.products.exception.ProductAlreadyExistsException;
import com.nicolasmesa.springboot.products.exception.ProductNotFoundException;
import com.nicolasmesa.springboot.products.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public Product getProductByName(String productName) {
        return productRepository.findProductByName(productName).orElseThrow(() -> new ProductNotFoundException(productName));
    }

    @Override
    public Product createProduct(Product product) {
        if (productRepository.findProductByName(product.getName()).isPresent())
            throw new ProductAlreadyExistsException(product.getName());
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) throw new ProductNotFoundException(id);
        productRepository.deleteById(id);
    }

    @Override
    public void updateProduct(Long id, Product product) {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setId(id);
        productRepository.save(product);
    }

    @Override
    public List<Product> getProductsWithStock() {
        return productRepository.findProductsWithStockAvailable();
    }

    @Override
    public List<Product> getProductsWithoutStock() {
        return productRepository.findProductsWithoutStockAvailable();
    }

    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    @Override
    public List<Product> getUnavailableProducts() {
        return productRepository.findUnavailableProducts();
    }

    @Override
    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findProductsByCategoryName(categoryName);
    }
}
