package com.nicolasmesa.springboot.usermanagement.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.usermanagement.dto.UserAccountDetailsDto;
import com.nicolasmesa.springboot.usermanagement.mapper.UserMapper;
import com.nicolasmesa.springboot.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService) {
        this.userService = userService;
        this.userMapper = UserMapper.INSTANCE;
    }

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<List<UserAccountDetailsDto>>> getUsers() {
        return ResponseMethods.ok(userService.getUsers().stream().map(userMapper::toDto).toList());
    }

    @GetMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> getUserByEmail(@PathVariable String email, @RequestHeader("X-GATEWAY-EMAIL") String authenticatedUserEmail) {
        return ResponseMethods.ok(userMapper.toDto(userService.getUserByEmail(email, authenticatedUserEmail)));
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> updateUser(@PathVariable String email, @Valid @RequestBody UserAccountDetailsDto updatedUser) {
        userService.updateUserAccountDetails(email, userMapper.toEntity(updatedUser));
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> deleteUser(@RequestParam String email) {
        userService.deleteUser(email);
        return ResponseMethods.noContent();
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> register(@Valid @RequestBody UserAccountDetailsDto user, @RequestHeader("X-GATEWAY-EMAIL") String authenticatedUserEmail) {

        userService.register(userMapper.toEntity(user), authenticatedUserEmail);
        return ResponseMethods.noContent();
    }
}
