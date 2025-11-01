package com.nicolasmesa.springboot.testcommon;

import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.nicolasmesa.springboot.common.Constants.DATE_FORMAT;
import static com.nicolasmesa.springboot.common.Constants.DATE_TIME_FORMAT;

public abstract class VerifyResponse<T> {
    protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    protected DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public void verifyErrors(ResultActions resultActions, List<String> errors) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").value(Matchers.hasSize(errors.size())));

        for (String error : errors) {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.errors").value(Matchers.hasItem(error)));
        }
    }

    public void verifyNoContent(ResultActions resultActions) throws Exception {
        resultActions
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty());
    }

    public abstract void verifyData(ResultActions resultActions, T data) throws Exception;

    public abstract void verifyData(ResultActions resultActions, List<T> data) throws Exception;
}
