package com.nicolasmesa.springboot.usermanagement.service;

import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.usermanagement.entity.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<ApiResponse<List<User>>> getUsers();

    ResponseEntity<ApiResponse<User>> getUserByEmail(String email, String emailAuthenticated);

    ResponseEntity<ApiResponse<User>> getUserById(Long id, String emailAuthenticated);

    ResponseEntity<ApiResponse<User>> register(User user);

    ResponseEntity<ApiResponse<User>> updateUser(Long id, User updatedUser);

    ResponseEntity<ApiResponse<User>> deleteUser(Long id);
}
