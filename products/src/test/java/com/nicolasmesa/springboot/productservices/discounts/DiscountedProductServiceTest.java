package com.nicolasmesa.springboot.productservices.discounts;

import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountNotFoundException;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountRepository;
import com.nicolasmesa.springboot.productservices.discounts.repository.DiscountedProductRepository;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountedProductService;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountedProductServiceImpl;
import com.nicolasmesa.springboot.productservices.discounts.service.ProductValidation;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DiscountedProductServiceTest extends DiscountGenerator {
    private DiscountedProductService discountedProductService;
    private DiscountedProductRepository discountedProductRepository;
    private DiscountRepository discountRepository;
    private ProductValidation productValidation;

    @BeforeTry
    void setup() {
        discountedProductRepository = Mockito.mock(DiscountedProductRepository.class);
        discountRepository = Mockito.mock(DiscountRepository.class);
        productValidation = Mockito.mock(ProductValidation.class);
        discountedProductService = new DiscountedProductServiceImpl(discountedProductRepository, discountRepository, productValidation);
    }

    @Property
    public void addDiscountToProducts(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        when(productValidation.existsBySlug(slug)).thenReturn(true);
        when(discountedProductRepository.existsByDiscountAndProductSlug(discount, slug)).thenReturn(false);

        discountedProductService.addDiscountToProducts(discount.getDiscountCode(), List.of(slug));
        List<DiscountedProduct> discountedProduct = List.of(new DiscountedProduct(discount, slug));

        verify(discountedProductRepository, times(1)).saveAll(discountedProduct);
    }

    @Property
    public void failedAddingDiscountToProductsInvalidDiscount(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));

        assertThrows(DiscountNotFoundException.class, () -> {
            discountedProductService.addDiscountToProducts(discount.getDiscountCode(), List.of(slug));
        });

        verifyNoInteractions(discountedProductRepository);
    }

    @Property
    public void failedAddingDiscountToProductsInvalidProduct(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        when(productValidation.existsBySlug(slug)).thenReturn(false);

        List<DiscountedProduct> discountedProducts = discountedProductService.addDiscountToProducts(discount.getDiscountCode(), List.of(slug));

        assertTrue(discountedProducts.isEmpty());
        verify(discountedProductRepository, times(0)).existsByDiscountAndProductSlug(discount, slug);
        verify(discountedProductRepository, times(1)).saveAll(List.of());
    }

    @Property
    public void failedAddingDiscountToProductsAlreadyDiscounted(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        when(productValidation.existsBySlug(slug)).thenReturn(true);
        when(discountedProductRepository.existsByDiscountAndProductSlug(discount, slug)).thenReturn(true);

        List<DiscountedProduct> discountedProduct = discountedProductService.addDiscountToProducts(discount.getDiscountCode(), List.of(slug));

        assertTrue(discountedProduct.isEmpty());
        verify(discountedProductRepository, times(1)).saveAll(List.of());
    }

    @Property
    public void getDiscountedProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) {
        List<DiscountedProduct> discountedProducts = slugs.stream().map(p -> new DiscountedProduct(discount, p)).toList();
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        when(discountedProductRepository.findAllByDiscount(discount)).thenReturn(discountedProducts);

        List<DiscountedProduct> results = discountedProductService.getDiscountedProducts(discount.getDiscountCode());

        for (int i = 0; i < slugs.size(); i++) {
            assertEquals(discountedProducts.get(i).getDiscount(), results.get(i).getDiscount());
            assertEquals(discountedProducts.get(i).getProductSlug(), results.get(i).getProductSlug());
        }
    }

    @Property
    public void failedGettingDiscountedProducts(@ForAll("genDiscount") Discount discount) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));

        assertThrows(DiscountNotFoundException.class, () -> {
            discountedProductService.getDiscountedProducts(discount.getDiscountCode());
        });

        verifyNoInteractions(discountedProductRepository);
    }

    @Property
    public void removeDiscountToProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) {
        List<DiscountedProduct> discountedProducts = slugs.stream().map(p -> new DiscountedProduct(discount, p)).toList();

        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        slugs.forEach(s -> when(productValidation.existsBySlug(s)).thenReturn(true));
        when(discountedProductRepository.findAllByDiscountAndProductSlugIn(discount, slugs)).thenReturn(discountedProducts);

        discountedProductService.removeDiscountToProducts(discount.getDiscountCode(), slugs);

        verify(discountedProductRepository, times(1)).deleteAll(discountedProducts);
    }

    @Property
    public void failedRemovingDiscountToProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));

        assertThrows(DiscountNotFoundException.class, () -> {
            discountedProductService.removeDiscountToProducts(discount.getDiscountCode(), slugs);
        });

        verifyNoInteractions(productValidation);
        verifyNoInteractions(discountedProductRepository);
    }

    @Property
    public void failedRemovingDiscountToInexistentProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        slugs.forEach(s -> when(productValidation.existsBySlug(s)).thenReturn(false));

        when(discountedProductRepository.findAllByDiscountAndProductSlugIn(discount, slugs)).thenReturn(List.of());
        discountedProductService.removeDiscountToProducts(discount.getDiscountCode(), slugs);

        verify(discountedProductRepository, times(0)).deleteAll();
    }

    @Property
    public void failedRemovingDiscountToProductsNotDiscounted(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) {
        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        slugs.forEach(s -> when(productValidation.existsBySlug(s)).thenReturn(true));
        when(discountedProductRepository.findAllByDiscountAndProductSlugIn(discount, slugs)).thenReturn(List.of());

        discountedProductService.removeDiscountToProducts(discount.getDiscountCode(), slugs);

        verify(discountedProductRepository, times(0)).deleteAll();
    }

    @Property
    public void removeDiscountToExistingProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) {
        List<DiscountedProduct> discountedProducts = slugs.stream().map(p -> new DiscountedProduct(discount, p)).toList();
        List<DiscountedProduct> validDiscountedProduct = List.of(discountedProducts.get(0));
        validDiscountedProduct.forEach(d -> d.setId((long) 0));
        List<String> validSlugs = validDiscountedProduct.stream().map(DiscountedProduct::getProductSlug).toList();

        when(discountRepository.findByDiscountCode(discount.getDiscountCode())).thenReturn(Optional.of(discount));
        when(productValidation.existsBySlug(anyString())).thenReturn(true, false);
        when(discountedProductRepository.findAllByDiscountAndProductSlugIn(discount, validSlugs)).thenReturn(validDiscountedProduct);

        discountedProductService.removeDiscountToProducts(discount.getDiscountCode(), slugs);

        verify(discountedProductRepository, times(1)).deleteAll(validDiscountedProduct);
    }
}
