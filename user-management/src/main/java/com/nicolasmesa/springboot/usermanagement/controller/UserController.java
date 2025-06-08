package com.nicolasmesa.springboot.usermanagement.controller;

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
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<Optional<User>> getUserByEmail(@PathVariable String email, @RequestHeader("email") String emailHeader) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();
        if (!(optionalUser.get().getEmailAddress().equals(emailHeader))) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(optionalUser);
    }

    @GetMapping(path = "/id/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id, @RequestHeader("email") String email) {
        Optional<User> user = userService.findById(id);
        if (user.isEmpty()) return ResponseEntity.notFound().build();
        if (!(user.get().getEmailAddress().equals(email))) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping(path = "/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        if (userService.findByEmail(user.getEmailAddress()).isPresent()) return ResponseEntity.notFound().build();
        userService.save(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        if (!userService.existsById(id)) return ResponseEntity.notFound().build();
        userService.save(updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        if (!userService.existsById(id)) return ResponseEntity.notFound().build();
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
