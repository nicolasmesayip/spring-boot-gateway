package com.nicolasmesa.springboot.productservices.products.repository;

import com.nicolasmesa.springboot.productservices.common.repository.SlugRepository;
import com.nicolasmesa.springboot.productservices.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, SlugRepository {
    List<Product> findByStockAvailableEquals(Integer stockThreshold);

    List<Product> findByStockAvailableGreaterThan(Integer stockThreshold);

    List<Product> findByIsAvailableFalse();

    List<Product> findByIsAvailableTrue();

    List<Product> findByCategorySlug(String categorySlug);

    Optional<Product> findByName(String name);

    Optional<Product> findBySlug(String slug);
}
