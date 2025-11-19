package com.nicolasmesa.springboot.productservices.discounts;

import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountNotFoundException;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountRepository;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountService;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountServiceImpl;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiscountServiceTest extends DiscountGenerator {
    private DiscountService discountService;
    private DiscountRepository discountRepository;

    @BeforeTry
    void setup() {
        discountRepository = Mockito.mock(DiscountRepository.class);
        discountService = new DiscountServiceImpl(discountRepository);
    }

    @Property
    public void getAllDiscounts(@ForAll("genListOfDiscounts") List<Discount> discounts) {
        for (int i = 0; i < discounts.size(); i++) {
            setOnCreationValues(discounts.get(i), (long) i);
        }

        when(discountRepository.findAll()).thenReturn(discounts);
        List<Discount> results = discountService.getAllDiscounts();

        for (int i = 0; i < discounts.size(); i++) {
            verifyDiscount(discounts.get(i), results.get(i));
        }
    }

    @Property
    public void getByDiscountCode(@ForAll("genDiscount") Discount discount) {
        setOnCreationValues(discount, (long) 0);

        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        Discount result = discountService.getByDiscountCode(discount.getDiscountCode());

        verifyDiscount(discount, result);
    }

    @Property
    public void failedGettingByDiscountCode(@ForAll("genDiscount") Discount discount) {
        setOnCreationValues(discount, (long) 0);

        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));
        assertThrows(DiscountNotFoundException.class, () -> {
            discountService.getByDiscountCode(discount.getDiscountCode());
        });
    }

    @Property
    public void createDiscount(@ForAll("genDiscount") Discount discount) {
        setOnCreationValues(discount, (long) 0);

        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.empty());

        discountService.createDiscount(discount);
        verify(discountRepository, times(1)).save(discount);
    }

    @Property
    public void failedCreatingDiscount(@ForAll("genDiscount") Discount discount) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));

        assertThrows(DiscountAlreadyExistsException.class, () -> {
            discountService.createDiscount(discount);
        });

        verify(discountRepository, times(0)).save(discount);
    }

    @Property
    public void deleteDiscount(@ForAll("genDiscount") Discount discount) {
        setOnCreationValues(discount, (long) 0);
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));

        discountService.deleteDiscount(discount.getDiscountCode());
        verify(discountRepository).deleteById(discount.getId());
    }

    @Property
    public void failedDeletingDiscount(@ForAll("genDiscount") Discount discount) {
        setOnCreationValues(discount, (long) 0);
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));

        assertThrows(DiscountNotFoundException.class, () -> {
            discountService.deleteDiscount(discount.getDiscountCode());
        });
    }

    public void verifyDiscount(Discount expected, Discount result) {
        assertNotNull(expected.getId());
        assertNotNull(expected.getUpdatedAt());
        assertNotNull(expected.getCreatedAt());
        assertNotNull(expected.getLastUsedAt());
        assertNotNull(expected.getTimesUsed());
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getDiscountCode(), result.getDiscountCode());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getDiscountType(), result.getDiscountType());
        assertEquals(expected.getDiscount(), result.getDiscount());
        assertEquals(expected.getCurrency(), result.getCurrency());
        assertEquals(expected.getMinimumPurchaseAmount(), result.getMinimumPurchaseAmount());
        assertEquals(expected.getIsActive(), result.getIsActive());
        assertEquals(expected.getMaxUses(), result.getMaxUses());
        assertEquals(expected.getIsStackable(), result.getIsStackable());
        assertEquals(expected.getStartDateTime(), result.getStartDateTime());
        assertEquals(expected.getEndDateTime(), result.getEndDateTime());
        assertEquals(expected.getUpdatedBy(), result.getUpdatedBy());
        assertEquals(expected.getCreatedBy(), result.getCreatedBy());
        assertEquals(expected.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(expected.getCreatedAt(), result.getCreatedAt());
        assertEquals(expected.getLastUsedAt(), result.getLastUsedAt());
        assertEquals(expected.getTimesUsed(), result.getTimesUsed());
    }

    public void setOnCreationValues(Discount discount, Long id) {
        LocalDateTime now = LocalDateTime.now();

        discount.setId(id);
        discount.setCreatedAt(now);
        discount.setUpdatedAt(now);
        discount.setLastUsedAt(now);
        discount.setTimesUsed(0);
    }
}
