package com.nicolasmesa.springboot.usermanagement.service;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.usermanagement.entity.User;
import com.nicolasmesa.springboot.usermanagement.exception.UserAlreadyExistsException;
import com.nicolasmesa.springboot.usermanagement.exception.UserNotFoundException;
import com.nicolasmesa.springboot.usermanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        return ResponseMethods.ok(userRepository.findAll());
    }

    @Override
    public ResponseEntity<ApiResponse<User>> getUserByEmail(String email, String emailAuthenticated) {
        if (!email.equals(emailAuthenticated)) return ResponseMethods.unAuthorized("Unauthorized");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        return ResponseMethods.ok(user);
    }

    @Override
    public ResponseEntity<ApiResponse<User>> getUserById(Long id, String emailAuthenticated) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (!user.getEmailAddress().equals(emailAuthenticated)) return ResponseMethods.unAuthorized("Unauthorized");
        return ResponseMethods.ok(user);
    }

    @Override
    public ResponseEntity<ApiResponse<User>> register(User user) {
        userRepository.findByEmail(user.getEmailAddress()).ifPresent(user1 -> {
            throw new UserAlreadyExistsException(user.getEmailAddress());
        });
        return ResponseMethods.ok(userRepository.save(user));
    }

    @Override
    public ResponseEntity<ApiResponse<User>> updateUser(Long id, User updatedUser) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return ResponseMethods.ok(userRepository.save(updatedUser));
    }

    @Override
    public ResponseEntity<ApiResponse<User>> deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return ResponseMethods.noContent();
    }
}
