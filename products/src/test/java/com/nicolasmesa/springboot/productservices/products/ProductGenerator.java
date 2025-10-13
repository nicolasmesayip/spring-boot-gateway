package com.nicolasmesa.springboot.productservices.products;

import com.nicolasmesa.springboot.productservices.common.SlugGenerator;
import com.nicolasmesa.springboot.productservices.products.dto.ProductDto;
import com.nicolasmesa.springboot.productservices.products.entity.Product;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

import java.util.List;

public class ProductGenerator extends SlugGenerator {
    @Provide
    Arbitrary<ProductDto> genProductDto() {
        return Combinators.combine(genStringLengthBetween1To50, genSlug(), genStringLengthBetween1To255, genStringLengthBetween1To50, genPositiveDouble, genCurrency, genPositiveInteger, genBoolean).as(ProductDto::new);
    }

    @Provide
    Arbitrary<Product> genProduct() {
        return genProductDto().map(dto -> {
            Product product = new Product();

            product.setName(dto.name());
            product.setSlug(dto.slug());
            product.setDescription(dto.description());
            product.setCurrency(dto.currency());
            product.setIsAvailable(dto.isAvailable());
            product.setPrice(dto.price());
            product.setStockAvailable(dto.stockAvailable());

            return product;
        });
    }

    @Provide
    Arbitrary<List<Product>> genListOfProducts() {
        return genProduct().list().ofMaxSize(10);
    }
}
