package com.nicolasmesa.springboot.productservices.categories;

import com.nicolasmesa.springboot.productservices.categories.dto.CategoryDto;
import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.util.List;

public class CategoryGenerator extends Generators {

    @Provide
    Arbitrary<CategoryDto> genCategoryDto() {
        Arbitrary<String> slug = genStringLengthBetween1To50.map(String::toLowerCase);

        return Combinators.combine(genStringLengthBetween1To50, slug, genStringLengthBetween1To255, genBoolean).as(CategoryDto::new);
    }

    @Provide
    Arbitrary<Category> genCategory() {
        return Combinators.combine(genStringLengthBetween1To50, genStringLengthBetween1To50, genStringLengthBetween1To255, genBoolean).as((name, slug, desc, isActive) -> {
            Category category = new Category();
            category.setName(name);
            category.setSlug(slug);
            category.setDescription(desc);
            category.setIsActive(isActive);
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
