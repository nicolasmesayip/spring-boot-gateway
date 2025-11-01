package com.nicolasmesa.springboot.usermanagement;

import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import com.nicolasmesa.springboot.testcommon.RequestBuilder;
import com.nicolasmesa.springboot.usermanagement.controller.UserAccountController;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import com.nicolasmesa.springboot.usermanagement.exception.ExceptionHandler;
import com.nicolasmesa.springboot.usermanagement.mapper.UserAccountMapperImpl;
import com.nicolasmesa.springboot.usermanagement.service.UserAccountServiceImpl;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

public class UserAccountControllerTest extends UserAccountGenerator {
    private UserAccountController userAccountController;
    private UserAccountServiceImpl userAccountService;
    private UserAccountMapperImpl userAccountMapper;
    private UserAccountControllerVerification userAccountControllerVerification;
    private MockMvc mockMvc;

    @BeforeTry
    void setup() {
        userAccountControllerVerification = new UserAccountControllerVerification();
        userAccountMapper = new UserAccountMapperImpl();
        userAccountService = Mockito.mock(UserAccountServiceImpl.class);
        userAccountController = new UserAccountController(userAccountService, userAccountMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(userAccountController).setControllerAdvice(new ExceptionHandler()).build();
    }

    @Property
    public void getUsers(@ForAll("genListOfAccountDetails") List<UserAccountDetails> userAccounts) throws Exception {
        Mockito.when(userAccountService.getUsers()).thenReturn(userAccounts);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/users/")).andExpect(MockMvcResultMatchers.status().isOk());

        userAccountControllerVerification.verifyData(resultActions, userAccountMapper.toDto(userAccounts));
    }

    @Property
    public void getUserByEmail(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.when(userAccountService.getUserByEmail(email, email)).thenReturn(userAccountDetails);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/users/{email}", email)
                .header("X-GATEWAY-EMAIL", email)).andExpect(MockMvcResultMatchers.status().isOk());

        userAccountControllerVerification.verifyData(resultActions, userAccountMapper.toDto(userAccountDetails));
    }

    @Property
    public void failedGettingUserByEmailUnauthorized(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.when(userAccountService.getUserByEmail(email, "")).thenThrow(new UnAuthorizedException());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/users/{email}", email)
                .header("X-GATEWAY-EMAIL", "")).andExpect(MockMvcResultMatchers.status().isUnauthorized());

        userAccountControllerVerification.verifyErrors(resultActions, List.of("You are not authorized to perform the action."));
    }

    @Property
    public void failedGettingInvalidUserByEmail(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.when(userAccountService.getUserByEmail(email, email)).thenThrow(new UserNotFoundException(email));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.get("/api/users/{email}", email)
                .header("X-GATEWAY-EMAIL", email)).andExpect(MockMvcResultMatchers.status().isNotFound());

        userAccountControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + email));
    }

    @Property
    public void updateUserAccountDetails(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.doNothing().when(userAccountService).updateUserAccountDetails(email, userAccountDetails);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.put("/api/users/{email}", email).body(userAccountMapper.toDto(userAccountDetails)));

        userAccountControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedUpdatingUserAccountDetails(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.doThrow(new UserNotFoundException(email)).when(userAccountService).updateUserAccountDetails(email, userAccountDetails);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.put("/api/users/{email}", email).body(userAccountMapper.toDto(userAccountDetails)));

        userAccountControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + email));
    }

    @Property
    public void deleteUser(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.doNothing().when(userAccountService).deleteUser(email);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/users/{email}", email));

        userAccountControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedDeletingUser(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.doThrow(new UserNotFoundException(email)).when(userAccountService).deleteUser(email);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/users/{email}", email));

        userAccountControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + email));
    }

    @Property
    public void register(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.doNothing().when(userAccountService).register(userAccountDetails, email);
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/users/register")
                .body(userAccountMapper.toDto(userAccountDetails))
                .header("X-GATEWAY-EMAIL", email));

        userAccountControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedRegisteringUnauthorized(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        Mockito.doThrow(new UnAuthorizedException()).when(userAccountService).register(userAccountDetails, "");
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/users/register")
                .body(userAccountMapper.toDto(userAccountDetails))
                .header("X-GATEWAY-EMAIL", ""));

        userAccountControllerVerification.verifyErrors(resultActions, List.of("You are not authorized to perform the action."));
    }

    @Property
    public void failedRegisteringUserAlreadyExists(@ForAll("genUserAccountDetails") UserAccountDetails userAccountDetails) throws Exception {
        String email = userAccountDetails.getEmailAddress();
        Mockito.doThrow(new UserAlreadyExistsException(email)).when(userAccountService).register(userAccountDetails, email);
        
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/users/register")
                .body(userAccountMapper.toDto(userAccountDetails))
                .header("X-GATEWAY-EMAIL", email));

        userAccountControllerVerification.verifyErrors(resultActions, List.of("User already exists with email: " + email));
    }
}
