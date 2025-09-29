package com.nicolasmesa.springboot.usermanagement.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.usermanagement.dto.UserAccountDetailsDto;
import com.nicolasmesa.springboot.usermanagement.mapper.UserAccountMapper;
import com.nicolasmesa.springboot.usermanagement.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final UserAccountMapper userAccountMapper;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        this.userAccountMapper = UserAccountMapper.INSTANCE;
    }

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<List<UserAccountDetailsDto>>> getUsers() {
        return ResponseMethods.ok(userAccountService.getUsers().stream().map(userAccountMapper::toDto).toList());
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> getUserByEmail(@PathVariable String email, @RequestHeader("X-GATEWAY-EMAIL") String authenticatedUserEmail) {
        return ResponseMethods.ok(userAccountMapper.toDto(userAccountService.getUserByEmail(email, authenticatedUserEmail)));
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> updateUser(@PathVariable String email, @Valid @RequestBody UserAccountDetailsDto updatedUser) {
        userAccountService.updateUserAccountDetails(email, userAccountMapper.toEntity(updatedUser));
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> deleteUser(@PathVariable String email) {
        userAccountService.deleteUser(email);
        return ResponseMethods.noContent();
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> register(@Valid @RequestBody UserAccountDetailsDto user, @RequestHeader("X-GATEWAY-EMAIL") String authenticatedUserEmail) {

        userAccountService.register(userAccountMapper.toEntity(user), authenticatedUserEmail);
        return ResponseMethods.noContent();
    }
}
