package com.nicolasmesa.springboot.productservices.categories.repository;

import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.productservices.common.repository.SlugRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, SlugRepository {
    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Optional<Category> findByName(String name);

    Optional<Category> findBySlug(String slug);
}
