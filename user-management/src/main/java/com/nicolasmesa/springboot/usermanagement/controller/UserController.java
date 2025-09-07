package com.nicolasmesa.springboot.usermanagement.controller;

import com.nicolasmesa.springboot.common.ResponseMethods;
import com.nicolasmesa.springboot.common.model.ApiResponse;
import com.nicolasmesa.springboot.usermanagement.entity.User;
import com.nicolasmesa.springboot.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        return ResponseMethods.ok(userService.findAll());
    }

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<ApiResponse<Optional<User>>> getUserByEmail(@PathVariable String email, @RequestHeader("email") String emailHeader) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) return ResponseMethods.notFound("User not found");
        if (!(optionalUser.get().getEmailAddress().equals(emailHeader))) return ResponseMethods.notFound("User not found");

        return ResponseMethods.ok(optionalUser);
    }

    @GetMapping(path = "/id/{id}")
    public ResponseEntity<ApiResponse<Optional<User>>> getUserById(@PathVariable Long id, @RequestHeader("email") String email) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) return ResponseMethods.notFound("User not found");
        if (!(user.get().getEmailAddress().equals(email))) return ResponseMethods.notFound("User not found");

        return ResponseMethods.ok(userService.findById(id));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
        if (userService.findByEmail(user.getEmailAddress()).isPresent()) return ResponseMethods.notFound("User not found");
        userService.save(user);
        return ResponseMethods.ok(user);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        if (!userService.existsById(id)) return ResponseMethods.notFound("User not found");
        userService.save(updatedUser);
        return ResponseMethods.ok(updatedUser);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable Long id) {
        if (!userService.existsById(id)) return ResponseMethods.notFound("User not found");
        userService.deleteById(id);
        return ResponseMethods.noContent();
    }
}
