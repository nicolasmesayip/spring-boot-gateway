package com.nicolasmesa.springboot.productservices.common.repository;

public interface SlugRepository {
    Boolean existsBySlug(String slug);
}