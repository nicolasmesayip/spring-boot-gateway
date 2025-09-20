package com.nicolasmesa.springboot.usermanagement.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.usermanagement.dto.UserDto;
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
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
        return ResponseMethods.ok(userService.getUsers().stream().map(userMapper::toDto).toList());
    }

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@PathVariable String email, @RequestHeader("email") String authenticatedUserEmail) {
        return ResponseMethods.ok(userMapper.toDto(userService.getUserByEmail(email, authenticatedUserEmail)));
    }

    @GetMapping(path = "/id/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id, @RequestHeader("email") String authenticatedUserEmail) {
        return ResponseMethods.ok(userMapper.toDto(userService.getUserById(id, authenticatedUserEmail)));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody UserDto user) {
        return ResponseMethods.created(userMapper.toDto(userService.register(userMapper.toEntity(user))));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto updatedUser) {
        userService.updateUser(id, userMapper.toEntity(updatedUser));
        return ResponseMethods.noContent();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<UserDto>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseMethods.noContent();
    }
}
