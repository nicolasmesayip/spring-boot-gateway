package com.nicolasmesa.springboot.productservices.common;

import com.nicolasmesa.springboot.common.exceptions.SlugAlreadyExistsException;
import com.nicolasmesa.springboot.common.validator.ValidSlug;
import com.nicolasmesa.springboot.productservices.common.repository.SlugRepository;
import com.nicolasmesa.springboot.productservices.common.service.SlugService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class SlugServiceTest extends SlugGenerator {

    private SlugRepository slugRepository;
    private SlugService slugService;
    private static Validator validator;

    @BeforeTry
    void setup() {
        slugRepository = Mockito.mock(SlugRepository.class);
        slugService = new SlugService();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    static class TestBean {
        @ValidSlug
        String slug;
    }

    @Property
    public void verifyingGivenSlug(@ForAll("genName") String name, @ForAll("genSlug") String slug) {
        String validSlug = slug.toLowerCase();
        Mockito.when(slugRepository.existsBySlug(validSlug)).thenReturn(false);

        String resultSlug = slugService.verifySlug(name, validSlug, slugRepository);
        assertEquals(validSlug, resultSlug);
    }

    @Property
    public void slugAlreadyExists(@ForAll("genName") String name, @ForAll("genSlug") String slug) {
        String validSlug = slug.toLowerCase();
        Mockito.when(slugRepository.existsBySlug(validSlug)).thenReturn(true);

        assertThrows(SlugAlreadyExistsException.class, () -> {
            slugService.verifySlug(name, validSlug, slugRepository);
        });
    }

    @Property
    public void creatingSlug(@ForAll("genName") String name) {
        Mockito.when(slugRepository.existsBySlug(anyString())).thenReturn(false);

        String result = slugService.verifySlug(name, null, slugRepository);
        assertNotNull(result);
        assertFalse(result.endsWith("-1"));

        TestBean bean = new TestBean();
        bean.slug = result;
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        Assertions.assertTrue(violations.isEmpty());
    }

    @Property
    public void creatingSlugWithExistingOne(@ForAll("genName") String name) {
        Mockito.when(slugRepository.existsBySlug(anyString())).thenReturn(true).thenReturn(false);

        String result = slugService.verifySlug(name, null, slugRepository);
        assertNotNull(result);
        assertTrue(result.endsWith("-1"));

        TestBean bean = new TestBean();
        bean.slug = result;
        Set<ConstraintViolation<TestBean>> violations = validator.validate(bean);
        Assertions.assertTrue(violations.isEmpty());
    }
}
