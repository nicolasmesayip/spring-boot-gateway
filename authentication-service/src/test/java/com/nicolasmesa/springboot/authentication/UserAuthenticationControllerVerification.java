package com.nicolasmesa.springboot.authentication;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.testcommon.VerifyResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

public class UserAuthenticationControllerVerification extends VerifyResponse<AuthResponse> {
    @Override
    public void verifyData(ResultActions resultActions, AuthResponse authResponse) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").value(authResponse.token()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.message").value(authResponse.message()));
    }

    @Override
    public void verifyData(ResultActions resultActions, List<AuthResponse> data) throws Exception {

    }
}
