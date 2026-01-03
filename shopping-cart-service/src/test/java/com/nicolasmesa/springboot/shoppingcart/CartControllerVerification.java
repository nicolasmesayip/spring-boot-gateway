package com.nicolasmesa.springboot.shoppingcart;

import com.nicolasmesa.springboot.shoppingcart.dto.CartDto;
import com.nicolasmesa.springboot.testcommon.VerifyResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

public class CartControllerVerification extends VerifyResponse<CartDto> {
    @Override
    public void verifyData(ResultActions resultActions, CartDto cart) throws Exception {
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());
        resultActions.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.userId").value(cart.userId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.currency").value(cart.currency().toString()));

        for (int i = 0; i < cart.items().size(); i++) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.items[" + i + "].productSlug").value(cart.items().get(i).productSlug()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.items[" + i + "].quantity").value(cart.items().get(i).quantity()));
        }
    }

    @Override
    public void verifyData(ResultActions resultActions, List<CartDto> data) throws Exception {

    }
}
