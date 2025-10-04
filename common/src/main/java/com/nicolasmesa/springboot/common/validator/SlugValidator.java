package com.nicolasmesa.springboot.common.validator;

import com.nicolasmesa.springboot.common.exceptions.InvalidSlugException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SlugValidator implements ConstraintValidator<ValidSlug, String> {

    private static final String SLUG_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    @Override
    public boolean isValid(String slug, ConstraintValidatorContext context) {
        if (slug == null || slug.isBlank()) {
            return true;
        }

        if (!slug.matches(SLUG_PATTERN)) {
            throw new InvalidSlugException(slug);
        }

        return true;
    }
}
