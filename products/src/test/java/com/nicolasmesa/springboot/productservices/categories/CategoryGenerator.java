package com.nicolasmesa.springboot.productservices.categories;

import com.nicolasmesa.springboot.productservices.categories.dto.CategoryDto;
import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.productservices.common.Generators;
import com.nicolasmesa.springboot.productservices.common.SlugGenerator;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.util.List;

public class CategoryGenerator extends Generators {

    @Provide
    Arbitrary<CategoryDto> genCategoryDto() {
        return Combinators.combine(genStringLengthBetween1To50, SlugGenerator.genSlug(), genStringLengthBetween1To255, genBoolean).as(CategoryDto::new);
    }

    @Provide
    Arbitrary<Category> genCategory() {
        return genCategoryDto().map(dto -> {
            Category category = new Category();
            category.setName(dto.name());
            category.setSlug(dto.slug());
            category.setDescription(dto.description());
            category.setIsActive(dto.isActive());
            return category;
        });
    }

    @Provide
    Arbitrary<List<Category>> genListOfCategories() {
        return genCategory().list().ofMaxSize(10);
    }

    @Provide
    Arbitrary<String> genDescription() {
        return genStringLengthBetween2To255;
    }
}
