package com.nicolasmesa.springboot.products.repository;

import com.nicolasmesa.springboot.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
