package com.nicolasmesa.springboot.productservices.products;

import com.nicolasmesa.springboot.common.exceptions.SlugAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.common.service.SlugService;
import com.nicolasmesa.springboot.productservices.products.entity.Product;
import com.nicolasmesa.springboot.productservices.products.exception.ProductAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.products.exception.ProductNotFoundException;
import com.nicolasmesa.springboot.productservices.products.repository.ProductRepository;
import com.nicolasmesa.springboot.productservices.products.service.ProductService;
import com.nicolasmesa.springboot.productservices.products.service.ProductServiceImpl;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductServiceTest extends ProductGenerator {
    private SlugService slugService;
    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeTry
    void setup() {
        slugService = Mockito.mock(SlugService.class);
        productRepository = Mockito.mock(ProductRepository.class);
        productService = new ProductServiceImpl(productRepository, slugService);
    }

    @Property
    public void getAllProducts(@ForAll("genListOfProducts") List<Product> products) {
        for (int i = 0; i < products.size(); i++) {
            setOnCreationValues(products.get(i), (long) i);
        }

        Mockito.when(productRepository.findAll()).thenReturn(products);

        List<Product> results = productService.getAllProducts();
        for (int i = 0; i < products.size(); i++) {
            Product expected = products.get(i);
            Product result = results.get(i);

            verifyProduct(expected, result);
        }
    }

    @Property
    public void getProductBySlug(@ForAll("genProduct") Product product) {
        setOnCreationValues(product, 1L);

        Mockito.when(productRepository.findBySlug(product.getSlug())).thenReturn(Optional.of(product));
        Product result = productService.getProductBySlug(product.getSlug());

        verifyProduct(product, result);
    }

    @Property
    public void failureGettingProductBySlug(@ForAll("genProduct") Product product) {
        setOnCreationValues(product, 1L);

        Mockito.when(productRepository.findBySlug(product.getSlug())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductBySlug(product.getSlug());
        });
    }

    @Property
    public void createProduct(@ForAll("genProduct") Product product) {
        setOnCreationValues(product, 1L);

        Mockito.when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        Mockito.when(slugService.verifySlug(product.getName(), product.getSlug(), productRepository)).thenReturn(product.getSlug());
        Mockito.when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);
        Mockito.verify(productRepository, Mockito.times(1)).save(product);

        verifyProduct(product, result);
    }

    @Property
    public void failureToCreateDuplicatedProduct(@ForAll("genProduct") Product product) {
        setOnCreationValues(product, 1L);
        Mockito.when(productRepository.findByName(product.getName())).thenReturn(Optional.of(product));

        assertThrows(ProductAlreadyExistsException.class, () -> {
            productService.createProduct(product);
        });
    }

    @Property
    public void failureToCreateProductWithInvalidSlug(@ForAll("genProduct") Product product) {
        productService = new ProductServiceImpl(productRepository, new SlugService());
        setOnCreationValues(product, 1L);
        Mockito.when(productRepository.findByName(product.getName())).thenReturn(Optional.empty());
        Mockito.when(productRepository.existsBySlug(product.getSlug())).thenReturn(true);

        assertThrows(SlugAlreadyExistsException.class, () -> {
            productService.createProduct(product);
        });
    }

    @Property
    public void deleteProduct(@ForAll("genProduct") Product product) {
        setOnCreationValues(product, 1L);
        Mockito.when(productRepository.findBySlug(product.getSlug())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getSlug());
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(product.getId());
    }

    @Property
    public void failureToDeleteInexistentProduct(@ForAll("genSlug") String slug) {
        Mockito.when(productRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(slug);
        });
    }

    @Property
    public void updateProduct(@ForAll("genProduct") Product originalProduct, @ForAll("genProduct") Product updatedProduct) {
        updatedProduct.setName(originalProduct.getName());
        updatedProduct.setSlug(originalProduct.getSlug());

        Mockito.when(productRepository.findBySlug(originalProduct.getSlug())).thenAnswer(p -> {
            setOnCreationValues(originalProduct, 1L);
            return Optional.of(originalProduct);
        });

        productService.updateProduct(originalProduct.getSlug(), updatedProduct);
        Mockito.verify(productRepository, Mockito.times(1)).save(updatedProduct);
    }

    @Property
    public void failedUpdatingInexistentProduct(@ForAll("genProduct") Product product) {
        Mockito.when(productRepository.findBySlug(product.getSlug())).thenThrow(ProductNotFoundException.class);

        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(product.getSlug(), product);
        });
    }

    @Property
    public void getProductsWithStock(@ForAll("genListOfProducts") List<Product> productsWithStock) {
        productsWithStock.forEach(x -> x.setStockAvailable(10));

        Mockito.when(productRepository.findByStockAvailableGreaterThan(0)).thenReturn(productsWithStock);
        List<Product> results = productService.getProductsWithStock();

        for (int i = 0; i < results.size(); i++) {
            verifyProduct(productsWithStock.get(i), results.get(i));
        }
    }

    @Property
    public void getProductsWithoutStock(@ForAll("genListOfProducts") List<Product> productsWithoutStock) {
        productsWithoutStock.forEach(x -> x.setStockAvailable(0));

        Mockito.when(productRepository.findByStockAvailableEquals(0)).thenReturn(productsWithoutStock);
        List<Product> results = productService.getProductsWithoutStock();

        for (int i = 0; i < results.size(); i++) {
            verifyProduct(productsWithoutStock.get(i), results.get(i));
        }
    }

    @Property
    public void getAvailableProducts(@ForAll("genListOfProducts") List<Product> availableProducts) {
        availableProducts.forEach(x -> x.setIsAvailable(true));

        Mockito.when(productRepository.findByIsAvailableTrue()).thenReturn(availableProducts);
        List<Product> results = productService.getAvailableProducts();

        for (int i = 0; i < results.size(); i++) {
            verifyProduct(availableProducts.get(i), results.get(i));
        }
    }

    @Property
    public void getUnAvailableProducts(@ForAll("genListOfProducts") List<Product> unavailableProducts) {
        unavailableProducts.forEach(x -> x.setIsAvailable(false));

        Mockito.when(productRepository.findByIsAvailableFalse()).thenReturn(unavailableProducts);
        List<Product> results = productService.getUnavailableProducts();

        for (int i = 0; i < results.size(); i++) {
            verifyProduct(unavailableProducts.get(i), results.get(i));
        }
    }

    @Property
    public void getProductsByCategorySlug(@ForAll("genListOfProducts") List<Product> products) {
        String categorySlug = "electronics";
        products.forEach(x -> x.setCategorySlug(categorySlug));

        Mockito.when(productRepository.findByCategorySlug(categorySlug)).thenReturn(products);
        List<Product> results = productService.getProductsByCategorySlug(categorySlug);

        for (int i = 0; i < results.size(); i++) {
            assertEquals(categorySlug, results.get(i).getCategorySlug());
            verifyProduct(products.get(i), results.get(i));
        }
    }

    @Property
    public void checkIfProductExistsBySlug(@ForAll("genProduct") Product product) {
        Boolean exists = Arbitraries.of(true, false).sample();

        Mockito.when(productRepository.existsBySlug(product.getSlug())).thenReturn(exists);
        Boolean result = productService.existsBySlug(product.getSlug());
        assertEquals(exists, result);
    }

    public void verifyProduct(Product expected, Product result) {
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getSlug(), result.getSlug());
        assertEquals(expected.getDescription(), result.getDescription());
        assertEquals(expected.getCategorySlug(), result.getCategorySlug());
        assertEquals(expected.getIsAvailable(), result.getIsAvailable());
        assertEquals(expected.getCurrency(), result.getCurrency());
        assertEquals(expected.getStockAvailable(), result.getStockAvailable());
        assertEquals(expected.getPrice(), result.getPrice());
        assertEquals(expected.getCreatedAt(), result.getCreatedAt());
        assertEquals(expected.getUpdatedAt(), result.getUpdatedAt());
    }

    public void setOnCreationValues(Product product, Long id) {
        LocalDateTime now = LocalDateTime.now();

        product.setId(id);
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
    }
}
