package com.nicolasmesa.springboot.products.repository;

import com.nicolasmesa.springboot.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, SlugRepository {
    @Query("SELECT p FROM Product p WHERE p.stockAvailable = 0")
    List<Product> findProductsWithoutStockAvailable();

    @Query("SELECT p FROM Product p WHERE p.stockAvailable > 0")
    List<Product> findProductsWithStockAvailable();

    @Query("SELECT p FROM Product p WHERE p.isAvailable = false")
    List<Product> findUnavailableProducts();

    @Query("SELECT p FROM Product p WHERE p.isAvailable = true")
    List<Product> findAvailableProducts();

    @Query("SELECT p FROM Product p WHERE p.category.name = :category")
    List<Product> findProductsByCategoryName(String category);

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findProductByName(String name);

    Optional<Product> findBySlug(String slug);
}
