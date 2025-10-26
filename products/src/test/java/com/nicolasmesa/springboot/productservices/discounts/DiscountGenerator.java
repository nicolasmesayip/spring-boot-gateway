package com.nicolasmesa.springboot.productservices.discounts;

import com.nicolasmesa.springboot.common.model.Currency;
import com.nicolasmesa.springboot.productservices.common.SlugGenerator;
import com.nicolasmesa.springboot.productservices.discounts.dto.DiscountDto;
import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountTypes;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

import java.time.LocalDateTime;
import java.util.List;

public class DiscountGenerator extends SlugGenerator {
    public static Arbitrary<DiscountTypes> genDiscountType = Arbitraries.of(DiscountTypes.class);

    @Provide
    Arbitrary<String> discountCode() {
        return Arbitraries.strings().withCharRange('A', 'Z')
                .numeric()
                .ofMinLength(6)
                .ofMaxLength(12)
                .map(String::toUpperCase);
    }

    @Provide
    Arbitrary<DiscountDto> genDiscountDto() {
        return Arbitraries.ofSuppliers(() -> {
            String discountCode = discountCode().sample();
            String description = genStringLengthBetween1To255.sample();
            DiscountTypes discountType = genDiscountType.sample();
            Double discount = genPositiveDouble.sample();
            Currency currency = genCurrency.sample();
            Double minimumPurchaseAmount = genPositiveDouble.sample();
            Boolean isActive = genBoolean.sample();
            Integer maxUses = genPositiveInteger.sample();
            Boolean isStackable = genBoolean.sample();
            LocalDateTime startDateTime = genLocalDateTimeInPresentOrFuture.sample();
            LocalDateTime endDateTime = genLocalDateTimeInFuture.sample();
            String updatedBy = genStringLengthBetween1To50.sample();
            String createdBy = genStringLengthBetween1To50.sample();

            return new DiscountDto(discountCode, description, discountType, discount, currency, minimumPurchaseAmount, isActive, maxUses, isStackable, startDateTime, endDateTime, updatedBy, createdBy);
        });
    }

    @Provide
    Arbitrary<Discount> genDiscount() {
        return genDiscountDto().map(dto -> {
            Discount discount = new Discount();

            discount.setDiscountCode(dto.discountCode());
            discount.setDescription(dto.description());
            discount.setDiscountType(dto.discountType());
            discount.setDiscount(dto.discount());
            discount.setCurrency(dto.currency());
            discount.setMinimumPurchaseAmount(dto.minimumPurchaseAmount());
            discount.setIsActive(dto.isActive());
            discount.setMaxUses(dto.maxUses());
            discount.setIsStackable(dto.isStackable());
            discount.setStartDateTime(dto.startDateTime());
            discount.setEndDateTime(dto.endDateTime());
            discount.setUpdatedBy(dto.updatedBy());
            discount.setCreatedBy(dto.updatedBy());

            return discount;
        });
    }

    @Provide
    Arbitrary<List<Discount>> genListOfDiscounts() {
        return genDiscount().list().ofMaxSize(10);
    }
}
