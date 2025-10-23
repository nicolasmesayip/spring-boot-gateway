package com.nicolasmesa.springboot.productservices.products;

import com.nicolasmesa.springboot.common.exceptions.SlugAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.products.controller.ProductController;
import com.nicolasmesa.springboot.productservices.products.dto.ProductDto;
import com.nicolasmesa.springboot.productservices.products.entity.Product;
import com.nicolasmesa.springboot.productservices.products.exception.ProductAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.products.exception.ProductExceptionHandler;
import com.nicolasmesa.springboot.productservices.products.exception.ProductNotFoundException;
import com.nicolasmesa.springboot.productservices.products.mapper.ProductMapper;
import com.nicolasmesa.springboot.productservices.products.mapper.ProductMapperImpl;
import com.nicolasmesa.springboot.productservices.products.service.ProductService;
import com.nicolasmesa.springboot.productservices.testcommon.RequestBuilder;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest extends ProductGenerator {
    private MockMvc mockMvc;
    private ProductMapper productMapper;
    private ProductService productService;
    private ProductControllerVerification productControllerVerification;

    @BeforeTry
    void setup() {
        productService = Mockito.mock(ProductService.class);
        productMapper = new ProductMapperImpl();
        ProductController controller = new ProductController(productService, productMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ProductExceptionHandler()).build();
        productControllerVerification = new ProductControllerVerification();
    }

    @Property(tries = 5)
    public void getAllProducts(@ForAll("genListOfProducts") List<Product> products) throws Exception {
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/products/"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        productControllerVerification.verifyData(resultActions, products);
    }

    @Property(tries = 5)
    public void createProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.when(productService.createProduct(product)).thenReturn(product);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/products/").body(dto))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        productControllerVerification.verifyData(resultActions, product);
    }

    @Property(tries = 5)
    public void failedCreatingProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.when(productService.createProduct(product)).thenThrow(new ProductAlreadyExistsException(product.getSlug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/products/").body(dto))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        productControllerVerification.verifyErrors(resultActions, List.of("Product already exists with product name: " + product.getSlug()));
    }

    @Property(tries = 5)
    public void failedCreatingProductInvalidSlug(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.when(productService.createProduct(product)).thenThrow(new SlugAlreadyExistsException(product.getSlug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/products/").body(dto))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        productControllerVerification.verifyErrors(resultActions, List.of("Slug '" + product.getSlug() + "' already exists."));
    }

    @Property(tries = 5)
    public void updateProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.doNothing().when(productService).updateProduct(product.getSlug(), product);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.put("/api/products/{slug}", product.getSlug()).body(dto));

        productControllerVerification.verifyNoContent(resultActions);
    }

    @Property(tries = 5)
    public void failedUpdatingProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.doThrow(new ProductNotFoundException(product.getSlug())).when(productService).updateProduct(product.getSlug(), product);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.put("/api/products/{slug}", product.getSlug()).body(dto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        productControllerVerification.verifyErrors(resultActions, List.of("Product not found with name: " + product.getSlug()));
    }

    @Property(tries = 5)
    public void deleteProduct(@ForAll("genSlug") String slug) throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(slug);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/products/{slug}", slug));

        productControllerVerification.verifyNoContent(resultActions);
    }

    @Property(tries = 5)
    public void failedDeletingProduct(@ForAll("genSlug") String slug) throws Exception {
        Mockito.doThrow(new ProductNotFoundException(slug)).when(productService).deleteProduct(slug);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/products/{slug}", slug))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        productControllerVerification.verifyErrors(resultActions, List.of("Product not found with name: " + slug));
    }

    @Property(tries = 5)
    public void getProductBySlug(@ForAll("genProduct") Product product) throws Exception {
        Mockito.when(productService.getProductBySlug(product.getSlug())).thenReturn(product);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/products/{slug}", product.getSlug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        productControllerVerification.verifyData(resultActions, product);
    }

    @Property(tries = 5)
    public void failedGettingProductBySlug(@ForAll("genProduct") Product product) throws Exception {
        Mockito.when(productService.getProductBySlug(product.getSlug())).thenThrow(new ProductNotFoundException(product.getSlug()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/products/{slug}", product.getSlug()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        productControllerVerification.verifyErrors(resultActions, List.of("Product not found with name: " + product.getSlug()));
    }

    @Property(tries = 5)
    public void getProductsWithStock(@ForAll("genListOf20Products") List<Product> products) throws Exception {
        for (int i = 0; i < products.size() / 2; i++) {
            products.get(i).setStockAvailable(0);
        }
        List<Product> productsWithStock = products.stream().filter(p -> p.getStockAvailable() > 0).toList();
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/"));

        Mockito.when(productService.getProductsWithStock()).thenReturn(productsWithStock);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/products/with-stock"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        productControllerVerification.verifyData(resultActions, productsWithStock);
    }

    @Property(tries = 5)
    public void getProductsWithoutStock(@ForAll("genListOf20Products") List<Product> products) throws Exception {
        for (int i = 0; i < products.size() / 2; i++) {
            products.get(i).setStockAvailable(0);
        }
        List<Product> productsWithoutStock = products.stream().filter(p -> p.getStockAvailable() == 0).toList();
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/"));

        Mockito.when(productService.getProductsWithoutStock()).thenReturn(productsWithoutStock);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/without-stock"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        productControllerVerification.verifyData(resultActions, productsWithoutStock);
    }

    @Property(tries = 5)
    public void getAvailableProducts(@ForAll("genListOf20Products") List<Product> products) throws Exception {
        for (int i = 0; i < products.size() / 2; i++) {
            products.get(i).setIsAvailable(true);
        }
        List<Product> availableProducts = products.stream().filter(Product::getIsAvailable).toList();
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/"));

        Mockito.when(productService.getAvailableProducts()).thenReturn(availableProducts);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/available"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        productControllerVerification.verifyData(resultActions, availableProducts);
    }

    @Property(tries = 5)
    public void getUnAvailableProducts(@ForAll("genListOf20Products") List<Product> products) throws Exception {
        for (int i = 0; i < products.size() / 2; i++) {
            products.get(i).setIsAvailable(false);
        }
        List<Product> unavailableProducts = products.stream().filter(p -> !p.getIsAvailable()).toList();
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/"));

        Mockito.when(productService.getUnavailableProducts()).thenReturn(unavailableProducts);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/unavailable"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        productControllerVerification.verifyData(resultActions, unavailableProducts);
    }

    @Property(tries = 5)
    public void getProductByCategory(@ForAll("genListOf20Products") List<Product> products) throws Exception {
        String categorySlug = "test-slug-1";
        for (int i = 0; i < products.size() / 2; i++) {
            products.get(i).setCategorySlug(categorySlug);
        }
        List<Product> productsWithCategory = products.stream().filter(p -> categorySlug.equals(p.getCategorySlug())).toList();
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/"));

        Mockito.when(productService.getProductsByCategorySlug(categorySlug)).thenReturn(productsWithCategory);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/category/{category}", categorySlug))
                .andExpect(MockMvcResultMatchers.status().isOk());

        productControllerVerification.verifyData(resultActions, productsWithCategory);
    }

    @Property(tries = 5)
    public void verifyProductBySlug(@ForAll("genSlug") String slug) throws Exception {
        Boolean exists = genBoolean.sample();
        Mockito.when(productService.existsBySlug(slug)).thenReturn(exists);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{slug}/exists", slug))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(exists.toString()));
    }
}
