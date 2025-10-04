package com.nicolasmesa.springboot.products.service;

import com.nicolasmesa.springboot.products.entity.Product;
import com.nicolasmesa.springboot.products.exception.ProductAlreadyExistsException;
import com.nicolasmesa.springboot.products.exception.ProductNotFoundException;
import com.nicolasmesa.springboot.products.repository.ProductRepository;
import com.nicolasmesa.springboot.products.service.utils.SlugService;
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
        if (productRepository.findProductByName(product.getName()).isPresent())
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
