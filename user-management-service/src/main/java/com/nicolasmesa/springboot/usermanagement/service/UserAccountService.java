package com.nicolasmesa.springboot.usermanagement.service;

import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;

import java.util.List;

public interface UserAccountService {
    List<UserAccountDetails> getUsers();

    UserAccountDetails getUserByEmail(String email, String authenticatedUserEmail);

    void updateUserAccountDetails(String email, UserAccountDetails updatedUserAccountDetails);

    void deleteUser(String email);

    void register(UserAccountDetails user, String authenticatedUserEmail);
}
