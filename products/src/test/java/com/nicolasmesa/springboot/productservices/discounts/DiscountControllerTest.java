package com.nicolasmesa.springboot.productservices.discounts;

import com.nicolasmesa.springboot.productservices.discounts.controller.DiscountController;
import com.nicolasmesa.springboot.productservices.discounts.dto.DiscountDto;
import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import com.nicolasmesa.springboot.productservices.discounts.entity.DiscountedProduct;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountAlreadyExistsException;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountExceptionHandler;
import com.nicolasmesa.springboot.productservices.discounts.exception.DiscountNotFoundException;
import com.nicolasmesa.springboot.productservices.discounts.mapper.DiscountMapper;
import com.nicolasmesa.springboot.productservices.discounts.mapper.DiscountMapperImpl;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountService;
import com.nicolasmesa.springboot.productservices.discounts.service.DiscountedProductService;
import com.nicolasmesa.springboot.productservices.testcommon.RequestBuilder;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class DiscountControllerTest extends DiscountGenerator {
    private DiscountService discountService;
    private DiscountedProductService discountedProductService;
    private DiscountMapper discountMapper;
    private DiscountControllerVerification discountControllerVerification;
    private MockMvc mockMvc;

    @BeforeTry
    void setup() {
        discountMapper = new DiscountMapperImpl();
        discountService = Mockito.mock(DiscountService.class);
        discountedProductService = Mockito.mock(DiscountedProductService.class);
        discountControllerVerification = new DiscountControllerVerification();
        DiscountController discountController = new DiscountController(discountService, discountedProductService, discountMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(discountController).setControllerAdvice(new DiscountExceptionHandler()).build();
    }

    @Property(tries = 5)
    public void getAllDiscounts(@ForAll("genListOfDiscounts") List<Discount> discounts) throws Exception {
        Mockito.when(discountService.getAllDiscounts()).thenReturn(discounts);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/discounts/"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        discountControllerVerification.verifyData(resultActions, discountMapper.toDto(discounts));
    }

    @Property(tries = 5)
    public void getByDiscountCode(@ForAll("genDiscount") Discount discount) throws Exception {
        Mockito.when(discountService.getByDiscountCode(discount.getDiscountCode())).thenReturn(discount);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/discounts/{discountCode}", discount.getDiscountCode()))
                .andExpect(MockMvcResultMatchers.status().isOk());

        discountControllerVerification.verifyData(resultActions, discountMapper.toDto(discount));
    }

    @Property(tries = 5)
    public void failedGettingByDiscountCode(@ForAll("genDiscount") Discount discount) throws Exception {
        Mockito.when(discountService.getByDiscountCode(discount.getDiscountCode())).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/discounts/{discountCode}", discount.getDiscountCode()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' was not found"));
    }

    @Property(tries = 5)
    public void createDiscount(@ForAll("genDiscount") Discount discount) throws Exception {
        DiscountDto dto = discountMapper.toDto(discount);
        Mockito.when(discountService.createDiscount(any())).thenReturn(discount);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/discounts/").body(dto))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        discountControllerVerification.verifyData(resultActions, dto);
    }

    @Property(tries = 5)
    public void failedCreatingDiscount(@ForAll("genDiscount") Discount discount) throws Exception {
        Mockito.when(discountService.createDiscount(any())).thenThrow(new DiscountAlreadyExistsException(discount.getDiscountCode()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/discounts/").body(discountMapper.toDto(discount)))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' already exists"));
    }

    @Property(tries = 5)
    public void deleteDiscount(@ForAll("genDiscount") Discount discount) throws Exception {
        Mockito.doNothing().when(discountService).deleteDiscount(discount.getDiscountCode());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/discounts/{discountCode}", discount.getDiscountCode()))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        discountControllerVerification.verifyNoContent(resultActions);
    }

    @Property(tries = 5)
    public void failedDeletingDiscount(@ForAll("genDiscount") Discount discount) throws Exception {
        Mockito.doThrow(new DiscountNotFoundException(discount.getDiscountCode())).when(discountService).deleteDiscount(discount.getDiscountCode());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/discounts/{discountCode}", discount.getDiscountCode()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' was not found"));
    }

    @Property(tries = 5)
    public void addDiscountToProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) throws Exception {
        List<DiscountedProduct> products = slugs.stream().map(slug -> new DiscountedProduct(discount, slug)).toList();

        Mockito.when(discountedProductService.addDiscountToProducts(discount.getDiscountCode(), slugs)).thenReturn(products);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/discounts/{discountCode}/add-products", discount.getDiscountCode())
                        .body(slugs))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist());

        for (String slug : slugs) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data").value(Matchers.hasItem(slug)));
        }
    }

    @Property(tries = 5)
    public void failedAddingDiscountToProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) throws Exception {
        Mockito.when(discountedProductService.addDiscountToProducts(discount.getDiscountCode(), slugs)).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/discounts/{discountCode}/add-products", discount.getDiscountCode())
                        .body(slugs))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' was not found"));
    }

    @Property(tries = 5)
    public void addDiscountToProduct(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) throws Exception {
        List<DiscountedProduct> products = List.of(new DiscountedProduct(discount, slug));

        Mockito.when(discountedProductService.addDiscountToProducts(discount.getDiscountCode(), List.of(slug))).thenReturn(products);

        mockMvc.perform(RequestBuilder.post("/api/discounts/{discountCode}/products/{productSlug}", discount.getDiscountCode(), slug))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(slug));

    }

    @Property(tries = 5)
    public void failedAddingDiscountToProduct(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) throws Exception {
        Mockito.when(discountedProductService.addDiscountToProducts(discount.getDiscountCode(), List.of(slug)))
                .thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/discounts/{discountCode}/products/{productSlug}", discount.getDiscountCode(), slug))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' was not found"));
    }

    @Property(tries = 5)
    public void removeDiscountToProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) throws Exception {
        Mockito.doNothing().when(discountedProductService).removeDiscountToProducts(discount.getDiscountCode(), slugs);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/discounts/{discountCode}/remove-products", discount.getDiscountCode())
                        .body(slugs))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        discountControllerVerification.verifyNoContent(resultActions);
    }

    @Property(tries = 5)
    public void failedRemovingDiscountToProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) throws Exception {
        Mockito.doThrow(new DiscountNotFoundException(discount.getDiscountCode())).when(discountedProductService).removeDiscountToProducts(discount.getDiscountCode(), slugs);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/discounts/{discountCode}/remove-products", discount.getDiscountCode())
                        .body(slugs))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' was not found"));
    }


    @Property(tries = 5)
    public void removeDiscountToProduct(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) throws Exception {
        Mockito.doNothing().when(discountedProductService).removeDiscountToProducts(discount.getDiscountCode(), List.of(slug));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/discounts/{discountCode}/products/{productSlug}", discount.getDiscountCode(), slug)
                        .body(slug))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        discountControllerVerification.verifyNoContent(resultActions);
    }

    @Property(tries = 5)
    public void failedRemovingDiscountToProduct(@ForAll("genDiscount") Discount discount, @ForAll("genSlug") String slug) throws Exception {
        Mockito.doThrow(new DiscountNotFoundException(discount.getDiscountCode())).when(discountedProductService).removeDiscountToProducts(discount.getDiscountCode(), List.of(slug));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/discounts/{discountCode}/products/{productSlug}", discount.getDiscountCode(), slug)
                        .body(slug))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' was not found"));
    }

    @Property(tries = 5)
    public void getDiscountedProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) throws Exception {
        List<DiscountedProduct> products = slugs.stream().map(slug -> new DiscountedProduct(discount, slug)).toList();

        Mockito.when(discountedProductService.getDiscountedProducts(discount.getDiscountCode())).thenReturn(products);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/discounts/{discountCode}/products", discount.getDiscountCode()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist());

        for (String slug : slugs) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data").value(Matchers.hasItem(slug)));
        }
    }

    @Property(tries = 5)
    public void failedGettingDiscountedProducts(@ForAll("genDiscount") Discount discount, @ForAll("genListOfSlugs") List<String> slugs) throws Exception {
        Mockito.when(discountedProductService.getDiscountedProducts(discount.getDiscountCode())).thenThrow(new DiscountNotFoundException(discount.getDiscountCode()));
        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/discounts/{discountCode}/products", discount.getDiscountCode()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        discountControllerVerification.verifyErrors(resultActions, List.of("Discount code '" + discount.getDiscountCode() + "' was not found"));
    }
}
