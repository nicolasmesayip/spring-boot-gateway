package com.nicolasmesa.springboot.usermanagement.controller;

import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.usermanagement.entity.User;
import com.nicolasmesa.springboot.usermanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/")
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        return userService.getUsers();
    }

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email, @RequestHeader("email") String emailHeader) {
        return userService.getUserByEmail(email, emailHeader);
    }

    @GetMapping(path = "/id/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id, @RequestHeader("email") String email) {
        return userService.getUserById(id, email);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        return userService.register(user);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
