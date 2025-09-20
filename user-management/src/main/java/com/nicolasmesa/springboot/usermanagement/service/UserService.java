package com.nicolasmesa.springboot.usermanagement.service;

import com.nicolasmesa.springboot.usermanagement.entity.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUserByEmail(String email, String emailAuthenticated);

    User getUserById(Long id, String emailAuthenticated);

    User register(User user);

    void updateUser(Long id, User updatedUser);

    void deleteUser(Long id);
}
