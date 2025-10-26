package com.nicolasmesa.springboot.productservices.discounts;

import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountRepository;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountedProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static com.nicolasmesa.springboot.productservices.testcommon.Utils.withClue;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DiscountEntityTest extends DiscountGenerator {

    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private DiscountedProductRepository discountedProductRepository;

    @Test
    public void testingOnCreationAndOnUpdateFunctionalities() throws InterruptedException {
        Discount discount = genDiscount().sample();
        Discount result = discountRepository.saveAndFlush(discount);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertNotNull(result.getTimesUsed());
        assertEquals(0, result.getTimesUsed());

        withClue("Testing the OnUpdate/PrePersist", () -> {
            LocalDateTime creationTime = result.getCreatedAt();
            LocalDateTime updatedTime = result.getUpdatedAt();

            Thread.sleep(5000);
            result.setDiscount(30.0);

            Discount result2 = discountRepository.saveAndFlush(result);

            assertTrue(updatedTime.isBefore(result2.getUpdatedAt()));
            assertEquals(creationTime, result2.getCreatedAt());
        });
    }

    @Test
    public void generationOfIdDiscountedProductEntity() {
        Discount discount = genDiscount().sample();
        discountRepository.saveAndFlush(discount);

        DiscountedProduct discountedProduct = new DiscountedProduct(discount, "product-slug-test");

        DiscountedProduct result = discountedProductRepository.saveAndFlush(discountedProduct);
        assertNotNull(result.getId());
    }
}
