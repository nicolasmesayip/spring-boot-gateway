package com.nicolasmesa.springboot.authentication;

import com.nicolasmesa.springboot.authentication.controller.UserAuthenticationController;
import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.dto.UserRegisterRequest;
import com.nicolasmesa.springboot.authentication.exception.AccountLockedException;
import com.nicolasmesa.springboot.authentication.exception.UserExceptionHandler;
import com.nicolasmesa.springboot.authentication.mapper.EmailVerificationMapper;
import com.nicolasmesa.springboot.authentication.mapper.EmailVerificationMapperImpl;
import com.nicolasmesa.springboot.authentication.service.RegistrationService;
import com.nicolasmesa.springboot.authentication.service.UserAuthenticationService;
import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import com.nicolasmesa.springboot.testcommon.RequestBuilder;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.lifecycle.BeforeTry;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserAuthenticationControllerTest extends UserAuthenticationGenerator {
    private MockMvc mockMvc;
    private UserAuthenticationService userAuthenticationService;
    private RegistrationService registrationService;
    private EmailVerificationMapper emailVerificationMapper;
    private UserAuthenticationControllerVerification userAuthenticationControllerVerification;

    @BeforeTry
    void setup() {
        userAuthenticationService = Mockito.mock(UserAuthenticationService.class);
        registrationService = Mockito.mock(RegistrationService.class);
        emailVerificationMapper = new EmailVerificationMapperImpl();
        userAuthenticationControllerVerification = new UserAuthenticationControllerVerification();
        mockMvc = MockMvcBuilders.standaloneSetup(new UserAuthenticationController(userAuthenticationService, registrationService, emailVerificationMapper))
                .setControllerAdvice(new UserExceptionHandler()).build();
    }

    @Property
    public void login(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto, @ForAll("genJwtToken") String jwtToken) throws Exception {
        AuthResponse authResponse = new AuthResponse(jwtToken, "Login successfully");
        Mockito.when(userAuthenticationService.login(userCredentialsDto)).thenReturn(authResponse);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/login").body(userCredentialsDto)).andExpect(MockMvcResultMatchers.status().isOk());
        userAuthenticationControllerVerification.verifyData(resultActions, authResponse);
    }

    @Property
    public void failedLoginUserNotFound(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.when(userAuthenticationService.login(userCredentialsDto)).thenThrow(new UserNotFoundException(userCredentialsDto.emailAddress()));
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/login").body(userCredentialsDto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + userCredentialsDto.emailAddress()));
    }

    @Property
    public void failedLoginUserAccountLocked(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.when(userAuthenticationService.login(userCredentialsDto)).thenThrow(new AccountLockedException());
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/login").body(userCredentialsDto));

        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("Account locked due to too many failed login attempts. Please contact support or try again later."));
    }

    @Property
    public void failedLoginInvalidCredentials(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.when(userAuthenticationService.login(userCredentialsDto)).thenThrow(new UnAuthorizedException());
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/login").body(userCredentialsDto))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("You are not authorized to perform the action."));
    }

    @Property
    public void resetPassword(@ForAll("genEmailVerificationDto") EmailVerificationDto emailVerificationDto) throws Exception {
        AuthResponse authResponse = new AuthResponse(null, "Email verification sent.");
        Mockito.when(userAuthenticationService.resetPassword(emailVerificationDto.emailAddress())).thenReturn(authResponse);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/resetPassword").body(emailVerificationDto)).andExpect(MockMvcResultMatchers.status().isOk());
        userAuthenticationControllerVerification.verifyData(resultActions, authResponse);
    }

    @Property
    public void failedResettingPasswordUserNotFound(@ForAll("genEmailVerificationDto") EmailVerificationDto emailVerificationDto) throws Exception {
        Mockito.when(userAuthenticationService.resetPassword(emailVerificationDto.emailAddress())).thenThrow(new UserNotFoundException(emailVerificationDto.emailAddress()));
        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/resetPassword").body(emailVerificationDto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + emailVerificationDto.emailAddress()));
    }

    @Property
    public void verifyOTPCode(@ForAll("genEmailVerificationDto") EmailVerificationDto emailVerificationDto) throws Exception {
        AuthResponse authResponse = new AuthResponse(null, "Email verified successfully.");
        Mockito.when(userAuthenticationService.verifyOTPCode(emailVerificationMapper.toEntity(emailVerificationDto))).thenReturn(authResponse);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/verifyOTPCode").body(emailVerificationDto)).andExpect(MockMvcResultMatchers.status().isOk());
        userAuthenticationControllerVerification.verifyData(resultActions, authResponse);
    }

    @Property
    public void failedVerifyingOTPCodeUserNotFound(@ForAll("genEmailVerificationDto") EmailVerificationDto emailVerificationDto) throws Exception {
        Mockito.when(userAuthenticationService.verifyOTPCode(emailVerificationMapper.toEntity(emailVerificationDto))).thenThrow(new UserNotFoundException(emailVerificationDto.emailAddress()));

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/verifyOTPCode").body(emailVerificationDto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + emailVerificationDto.emailAddress()));
    }

    @Property
    public void updatePassword(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.doNothing().when(userAuthenticationService).updatePasswordRequest(userCredentialsDto);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/updatePassword").body(userCredentialsDto));
        userAuthenticationControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedUpdatingPasswordUserNotFound(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.doThrow(new UserNotFoundException(userCredentialsDto.emailAddress())).when(userAuthenticationService).updatePasswordRequest(userCredentialsDto);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/updatePassword").body(userCredentialsDto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + userCredentialsDto.emailAddress()));
    }

    @Property
    public void register(@ForAll("genUserRegisterRequest") UserRegisterRequest userRegisterRequest) throws Exception {
        Mockito.doNothing().when(registrationService).register(userRegisterRequest.userCredentials(), userRegisterRequest.userAccountDetails());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/register").body(userRegisterRequest));
        userAuthenticationControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedRegisteringUserAlreadyExists(@ForAll("genUserRegisterRequest") UserRegisterRequest userRegisterRequest) throws Exception {
        Mockito.doThrow(new UserAlreadyExistsException(userRegisterRequest.userCredentials().emailAddress())).when(registrationService).register(userRegisterRequest.userCredentials(), userRegisterRequest.userAccountDetails());

        ResultActions resultActions = mockMvc.perform(RequestBuilder.post("/api/auth/register").body(userRegisterRequest));
        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("User already exists with email: " + userRegisterRequest.userCredentials().emailAddress()));
    }

    @Property
    public void deleteAccount(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.doNothing().when(registrationService).deleteAccount(userCredentialsDto);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/auth/{email}", userCredentialsDto.emailAddress()).body(userCredentialsDto));
        userAuthenticationControllerVerification.verifyNoContent(resultActions);
    }

    @Property
    public void failedDeletingAccountUserNotFound(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.doThrow(new UserNotFoundException(userCredentialsDto.emailAddress())).when(registrationService).deleteAccount(userCredentialsDto);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/auth/{email}", userCredentialsDto.emailAddress()).body(userCredentialsDto));
        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("User not found with email: " + userCredentialsDto.emailAddress()));
    }

    @Property
    public void failedDeletingAccountInvalidCredentials(@ForAll("genUserCredentialsDto") UserCredentialsDto userCredentialsDto) throws Exception {
        Mockito.doThrow(new UnAuthorizedException()).when(registrationService).deleteAccount(userCredentialsDto);

        ResultActions resultActions = mockMvc.perform(RequestBuilder.delete("/api/auth/{email}", userCredentialsDto.emailAddress()).body(userCredentialsDto));
        userAuthenticationControllerVerification.verifyErrors(resultActions, List.of("You are not authorized to perform the action."));
    }
}
