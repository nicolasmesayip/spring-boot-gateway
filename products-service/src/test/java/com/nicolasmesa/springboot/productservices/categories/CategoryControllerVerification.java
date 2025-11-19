package com.nicolasmesa.springboot.productservices.categories;

import com.nicolasmesa.springboot.productservices.categories.entity.Category;
import com.nicolasmesa.springboot.testcommon.VerifyResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

public class CategoryControllerVerification extends VerifyResponse<Category> {
    @Override
    public void verifyData(ResultActions resultActions, Category category) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(category.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.slug").value(category.getSlug()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(category.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isActive").value(category.getIsActive()));
    }

    @Override
    public void verifyData(ResultActions resultActions, List<Category> listOfCategories) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist());

        for (int i = 0; i < listOfCategories.size(); i++) {
            Category category = listOfCategories.get(i);

            resultActions
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].name").value(category.getName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].slug").value(category.getSlug()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].description").value(category.getDescription()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].isActive").value(category.getIsActive()));
        }
    }
}
