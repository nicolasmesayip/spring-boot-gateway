package com.nicolasmesa.springboot.usermanagement.service;

import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import com.nicolasmesa.springboot.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserAccountDetails> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserAccountDetails getUserByEmail(String email, String authenticatedUserEmail) {
        if (!email.equals(authenticatedUserEmail)) throw new UnAuthorizedException();
        return userRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public void updateUserAccountDetails(String email, UserAccountDetails updatedUserAccountDetails) {
        UserAccountDetails userAccountDetails = userRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
        userAccountDetails.setEmailAddress(email);

        userRepository.save(updatedUserAccountDetails);
    }

    @Override
    public void deleteUser(String email) {
        userRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
        userRepository.deleteById(email);
    }

    @Override
    public void register(UserAccountDetails user, String authenticatedUserEmail) {
        if (authenticatedUserEmail.isEmpty()) throw new UnAuthorizedException();
        if (userRepository.existsById(user.getEmailAddress()))
            throw new UserAlreadyExistsException(user.getEmailAddress());

        userRepository.save(user);
    }
}
