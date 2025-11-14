package com.nicolasmesa.springboot.authentication.controller;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.dto.UserRegisterRequest;
import com.nicolasmesa.springboot.authentication.mapper.EmailVerificationMapper;
import com.nicolasmesa.springboot.authentication.service.RegistrationService;
import com.nicolasmesa.springboot.authentication.service.UserAuthenticationService;
import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
@Validated
public class UserAuthenticationController {
    private final UserAuthenticationService userAuthenticationService;
    private final RegistrationService registrationService;
    private final EmailVerificationMapper emailVerificationMapper;

    public UserAuthenticationController(UserAuthenticationService userAuthenticationService, RegistrationService registrationService, EmailVerificationMapper emailVerificationMapper) {
        this.userAuthenticationService = userAuthenticationService;
        this.registrationService = registrationService;
        this.emailVerificationMapper = emailVerificationMapper;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody @NotNull UserCredentialsDto credentials) {
        return ResponseMethods.ok(userAuthenticationService.login(credentials));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody @NotNull UserRegisterRequest userRegisterRequest) {
        registrationService.register(userRegisterRequest.userCredentials(), userRegisterRequest.userAccountDetails());
        return ResponseMethods.noContent();
    }

    @PostMapping(path = "/resetPassword")
    public ResponseEntity<ApiResponse<AuthResponse>> resetPassword(@Valid @RequestBody @NotNull EmailVerificationDto emailAddress) {
        return ResponseMethods.ok(userAuthenticationService.resetPassword(emailAddress.emailAddress()));
    }

    @PostMapping(path = "/verifyOTPCode")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOTPCode(@Valid @RequestBody @NotNull EmailVerificationDto emailVerificationDto) {
        return ResponseMethods.ok(userAuthenticationService.verifyOTPCode(emailVerificationMapper.toEntity(emailVerificationDto)));
    }

    @PostMapping(path = "/updatePassword")
    public ResponseEntity<ApiResponse<AuthResponse>> updatePassword(@Valid @RequestBody @NotNull UserCredentialsDto credentials) {
        userAuthenticationService.updatePasswordRequest(credentials);
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<AuthResponse>> deleteAccount(@Valid @RequestBody @NotNull UserCredentialsDto credentials) {
        registrationService.deleteAccount(credentials);
        return ResponseMethods.noContent();
    }
}
