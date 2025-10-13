package com.nicolasmesa.springboot.productservices.products.service;

import com.nicolasmesa.springboot.productservices.common.service.SlugService;
import com.nicolasmesa.springboot.productservices.products.entity.Product;
import com.nicolasmesa.springboot.productservices.products.exception.ProductAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.products.exception.ProductNotFoundException;
import com.nicolasmesa.springboot.productservices.products.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final SlugService slugService;

    public ProductServiceImpl(ProductRepository productRepository, SlugService slugService) {
        this.productRepository = productRepository;
        this.slugService = slugService;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductBySlug(String productName) {
        return productRepository.findBySlug(productName).orElseThrow(() -> new ProductNotFoundException(productName));
    }

    @Override
    public Product createProduct(Product product) {
        if (productRepository.findByName(product.getName()).isPresent())
            throw new ProductAlreadyExistsException(product.getName());

        product.setSlug(slugService.verifySlug(product.getName(), product.getSlug(), productRepository));

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(String slug) {
        Product product = productRepository.findBySlug(slug).orElseThrow(() -> new ProductNotFoundException(slug));
        productRepository.deleteById(product.getId());
    }

    @Override
    public void updateProduct(String slug, Product product) {
        Product p = productRepository.findBySlug(slug).orElseThrow(() -> new ProductNotFoundException(slug));
        product.setId(p.getId());
        productRepository.save(product);
    }

    @Override
    public List<Product> getProductsWithStock() {
        return productRepository.findByStockAvailableGreaterThan(0);
    }

    @Override
    public List<Product> getProductsWithoutStock() {
        return productRepository.findByStockAvailableEquals(0);
    }

    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findByIsAvailableTrue();
    }

    @Override
    public List<Product> getUnavailableProducts() {
        return productRepository.findByIsAvailableFalse();
    }

    @Override
    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    @Override
    public Boolean existsBySlug(String slug) {
        return productRepository.existsBySlug(slug);
    }
}
