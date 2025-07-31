package com.nicolasmesa.springboot.products.repository;

import com.nicolasmesa.springboot.products.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
