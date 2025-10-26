package com.nicolasmesa.springboot.productservices.categories;

import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.productservices.categories.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static com.nicolasmesa.springboot.productservices.testcommon.Utils.withClue;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CategoryEntityTest extends CategoryGenerator {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testingOnCreationAndOnUpdateFunctionalities() throws InterruptedException {
        Category category = genCategory().sample();
        Category result = categoryRepository.save(category);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        withClue("Testing the OnUpdate/PrePersist", () -> {
            LocalDateTime creationTime = result.getCreatedAt();
            LocalDateTime updatedTime = result.getUpdatedAt();

            Thread.sleep(5000);
            result.setDescription("New Description");

            Category result2 = categoryRepository.saveAndFlush(result);

            assertTrue(updatedTime.isBefore(result2.getUpdatedAt()));
            assertEquals(creationTime, result2.getCreatedAt());
        });
    }
}
