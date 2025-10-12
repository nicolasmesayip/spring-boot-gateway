package com.nicolasmesa.springboot.productservices.common;

import com.nicolasmesa.springboot.common.validator.ValidSlug;
import jakarta.validation.*;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.Assertions;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SlugValidatorTest extends SlugGenerator {

    private static Validator validator;

    @BeforeTry
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class TestBean {
        @ValidSlug
        String slug;
    }

    @Property
    public void validatingSlug(@ForAll("genSlug") String slug) {
        TestBean bean = new TestBean();
        bean.slug = slug;

        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Property
    public void validatingInvalidSlug() {
        String invalidSlug = "Invalid Slug 7";
        TestBean bean = new TestBean();
        bean.slug = invalidSlug;

        assertThrows(ValidationException.class, () -> {
            Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
            assertEquals(1, violations.size());
            assertEquals("Slug '" + invalidSlug + "' is not valid. Use only lowercase letters, numbers, and hyphens.", violations.iterator().next().getMessage());
        });
    }

    @Property
    public void validatingEmptySlug() {
        TestBean bean = new TestBean();
        bean.slug = "";

        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Property
    public void validatingNullSlug() {
        TestBean bean = new TestBean();
        bean.slug = null;

        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        Assertions.assertTrue(violations.isEmpty());
    }
}
