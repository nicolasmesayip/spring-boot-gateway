package com.nicolasmesa.springboot.usermanagement;

import com.nicolasmesa.springboot.common.dto.UserAccountDetailsDto;
import com.nicolasmesa.springboot.testcommon.VerifyResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

public class UserAccountControllerVerification extends VerifyResponse<UserAccountDetailsDto> {

    @Override
    public void verifyData(ResultActions resultActions, UserAccountDetailsDto userAccountDetails) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.firstName").value(userAccountDetails.firstName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.lastName").value(userAccountDetails.lastName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.emailAddress").value(userAccountDetails.emailAddress()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.countryCode").value(userAccountDetails.countryCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.mobileNumber").value(userAccountDetails.mobileNumber()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.homeAddress").value(userAccountDetails.homeAddress()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.dateOfBirth").value(userAccountDetails.dateOfBirth().format(dateFormatter)));
    }

    @Override
    public void verifyData(ResultActions resultActions, List<UserAccountDetailsDto> userAccounts) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist());

        for (int i = 0; i < userAccounts.size(); i++) {
            UserAccountDetailsDto userAccountDetails = userAccounts.get(i);

            resultActions
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].firstName").value(userAccountDetails.firstName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].lastName").value(userAccountDetails.lastName()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].emailAddress").value(userAccountDetails.emailAddress()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].countryCode").value(userAccountDetails.countryCode()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].mobileNumber").value(userAccountDetails.mobileNumber()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].homeAddress").value(userAccountDetails.homeAddress()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data[" + i + "].dateOfBirth").value(userAccountDetails.dateOfBirth().format(dateFormatter)));
        }
    }
}
