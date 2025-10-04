package com.nicolasmesa.springboot.products.service.utils;

import com.nicolasmesa.springboot.common.exceptions.SlugAlreadyExistsException;
import com.nicolasmesa.springboot.products.repository.SlugRepository;
import org.springframework.stereotype.Service;

@Service
public class SlugService {

    public String generateSlug(String name, SlugRepository repository) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        String slug = baseSlug;
        int counter = 1;

        while (repository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    public String verifySlug(String name, String slug, SlugRepository repository) {
        if (slug != null) {
            if (repository.existsBySlug(slug)) throw new SlugAlreadyExistsException(slug);
            else return slug;
        } else {
            return generateSlug(name, repository);
        }
    }
}
