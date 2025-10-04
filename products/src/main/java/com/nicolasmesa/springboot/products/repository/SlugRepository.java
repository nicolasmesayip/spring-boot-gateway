package com.nicolasmesa.springboot.products.repository;

public interface SlugRepository {
    Boolean existsBySlug(String slug);
}