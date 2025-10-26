package com.nicolasmesa.springboot.productservices.products;

import com.nicolasmesa.springboot.productservices.products.entity.Product;
import com.nicolasmesa.springboot.productservices.testcommon.VerifyResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

public class ProductControllerVerification extends VerifyResponse<Product> {
    @Override
    public void verifyData(ResultActions resultActions, Product product) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
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

    @Override
    public void verifyData(ResultActions resultActions, List<Product> products) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
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
}
