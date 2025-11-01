package com.nicolasmesa.springboot.productservices.discounts;

import com.nicolasmesa.springboot.productservices.discounts.dto.DiscountDto;
import com.nicolasmesa.springboot.testcommon.VerifyResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

public class DiscountControllerVerification extends VerifyResponse<DiscountDto> {

    @Override
    public void verifyData(ResultActions resultActions, DiscountDto discount) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.discountCode").value(discount.discountCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.description").value(discount.description()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.discountType").value(discount.discountType().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.discount").value(discount.discount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.currency").value(discount.currency().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.minimumPurchaseAmount").value(discount.minimumPurchaseAmount()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isActive").value(discount.isActive()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.maxUses").value(discount.maxUses()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isStackable").value(discount.isStackable()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.startDateTime").value(discount.startDateTime().format(dateTimeFormatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.endDateTime").value(discount.endDateTime().format(dateTimeFormatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.createdBy").value(discount.createdBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.updatedBy").value(discount.updatedBy()));
    }

    @Override
    public void verifyData(ResultActions resultActions, List<DiscountDto> discounts) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist());

        for (int i = 0; i < discounts.size(); i++) {
            DiscountDto discount = discounts.get(i);

            resultActions
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].discountCode").value(discount.discountCode()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].description").value(discount.description()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].discountType").value(discount.discountType().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].discount").value(discount.discount()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].currency").value(discount.currency().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].minimumPurchaseAmount").value(discount.minimumPurchaseAmount()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].isActive").value(discount.isActive()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].maxUses").value(discount.maxUses()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].isStackable").value(discount.isStackable()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].startDateTime").value(discount.startDateTime().format(formatter)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].endDateTime").value(discount.endDateTime().format(formatter)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].createdBy").value(discount.createdBy()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].updatedBy").value(discount.updatedBy()));
        }
    }
}
