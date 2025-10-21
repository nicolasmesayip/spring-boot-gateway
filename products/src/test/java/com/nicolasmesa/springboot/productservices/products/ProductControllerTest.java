package com.nicolasmesa.springboot.productservices.products;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
    private ObjectMapper objectMapper;

    @BeforeTry
    void setup() {
        productService = Mockito.mock(ProductService.class);
        productMapper = new ProductMapperImpl();
        ProductController controller = new ProductController(productService, productMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ProductExceptionHandler()).build();
        objectMapper = new ObjectMapper();
    }

    @Property(tries = 5)
    public void getAllProducts(@ForAll("genListOfProducts") List<Product> products) throws Exception {
        Mockito.when(productService.getAllProducts()).thenReturn(products);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/"));
        verifyProduct(resultActions, products);
    }

    @Property(tries = 5)
    public void createProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.when(productService.createProduct(product)).thenReturn(product);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/products/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
        verifyProduct(resultActions, product);
    }

    @Property(tries = 5)
    public void failedCreatingProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.when(productService.createProduct(product)).thenThrow(new ProductAlreadyExistsException(product.getSlug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/products/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        verifyErrors(resultActions, List.of("Product already exists with product name: " + product.getSlug()));
    }

    @Property(tries = 5)
    public void failedCreatingProductInvalidSlug(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.when(productService.createProduct(product)).thenThrow(new SlugAlreadyExistsException(product.getSlug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/products/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        verifyErrors(resultActions, List.of("Slug '" + product.getSlug() + "' already exists."));
    }

    @Property(tries = 5)
    public void updateProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.doNothing().when(productService).updateProduct(product.getSlug(), product);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/{slug}", product.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Property(tries = 5)
    public void failedUpdatingProduct(@ForAll("genProduct") Product product) throws Exception {
        ProductDto dto = productMapper.toDto(product);
        Mockito.doThrow(new ProductNotFoundException(product.getSlug())).when(productService).updateProduct(product.getSlug(), product);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/products/{slug}", product.getSlug())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verifyErrors(resultActions, List.of("Product not found with name: " + product.getSlug()));
    }

    @Property(tries = 5)
    public void deleteProduct(@ForAll("genSlug") String slug) throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(slug);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/{slug}", slug))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    @Property(tries = 5)
    public void failedDeletingProduct(@ForAll("genSlug") String slug) throws Exception {
        Mockito.doThrow(new ProductNotFoundException(slug)).when(productService).deleteProduct(slug);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/{slug}", slug))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verifyErrors(resultActions, List.of("Product not found with name: " + slug));
    }

    @Property(tries = 5)
    public void getProductBySlug(@ForAll("genProduct") Product product) throws Exception {
        Mockito.when(productService.getProductBySlug(product.getSlug())).thenReturn(product);

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{slug}", product.getSlug()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verifyProduct(resultActions, product);
    }

    @Property(tries = 5)
    public void failedGettingProductBySlug(@ForAll("genProduct") Product product) throws Exception {
        Mockito.when(productService.getProductBySlug(product.getSlug())).thenThrow(new ProductNotFoundException(product.getSlug()));

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{slug}", product.getSlug()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verifyErrors(resultActions, List.of("Product not found with name: " + product.getSlug()));
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
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/with-stock"));
        verifyProduct(resultActions, productsWithStock);
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
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/without-stock"));
        verifyProduct(resultActions, productsWithoutStock);
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
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/available"));
        verifyProduct(resultActions, availableProducts);
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
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/unavailable"));
        verifyProduct(resultActions, unavailableProducts);
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
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/products/category/{category}", categorySlug));
        verifyProduct(resultActions, productsWithCategory);
    }

    @Property(tries = 5)
    public void verifyProductBySlug(@ForAll("genSlug") String slug) throws Exception {
        Boolean exists = genBoolean.sample();
        Mockito.when(productService.existsBySlug(slug)).thenReturn(exists);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/{slug}/exists", slug))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(exists.toString()));
    }

    public void verifyProduct(ResultActions resultActions, Product product) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(product.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.slug").value(product.getSlug()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(product.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.categorySlug").value(product.getCategorySlug()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.price").value(product.getPrice()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.currency").value(product.getCurrency().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.stockAvailable").value(product.getStockAvailable()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isAvailable").value(product.getIsAvailable()));
    }

    public void verifyProduct(ResultActions resultActions, List<Product> products) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist());

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            resultActions
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].name").value(product.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].slug").value(product.getSlug()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].description").value(product.getDescription()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].categorySlug").value(product.getCategorySlug()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].price").value(product.getPrice()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].currency").value(product.getCurrency().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].stockAvailable").value(product.getStockAvailable()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].isAvailable").value(product.getIsAvailable()));
        }

    }

    public void verifyErrors(ResultActions resultActions, List<String> errors) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").value(Matchers.hasSize(errors.size())));

        for (String error : errors) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.errors").value(Matchers.hasItem(error)));
        }
    }
}
