package com.nicolasmesa.springboot.productservices.products;

import com.nicolasmesa.springboot.productservices.products.entity.Product;
import com.nicolasmesa.springboot.productservices.products.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static com.nicolasmesa.springboot.productservices.testcommon.Utils.withClue;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductEntityTest extends ProductGenerator {

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testingOnCreationAndOnUpdateFunctionalities() throws InterruptedException {
        Product product = genProduct().sample();
        Product result = productRepository.save(product);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        withClue("Testing the OnUpdate/PrePersist", () -> {
            LocalDateTime creationTime = result.getCreatedAt();
            LocalDateTime updatedTime = result.getUpdatedAt();

            Thread.sleep(5000);
            result.setDescription("New Description");

            Product result2 = productRepository.saveAndFlush(result);

            assertTrue(updatedTime.isBefore(result2.getUpdatedAt()));
            assertEquals(creationTime, result2.getCreatedAt());
        });
    }
}
