package com.nicolasmesa.springboot.usermanagement.service;

import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.usermanagement.entity.User;
import com.nicolasmesa.springboot.usermanagement.exception.UserAlreadyExistsException;
import com.nicolasmesa.springboot.usermanagement.exception.UserNotFoundException;
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
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email, String authenticatedUserEmail) {
        if (!email.equals(authenticatedUserEmail)) throw new UnAuthorizedException();
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public User getUserById(Long id, String authenticatedUserEmail) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (!user.getEmailAddress().equals(authenticatedUserEmail)) throw new UnAuthorizedException();

        return user;
    }

    @Override
    public User register(User user) {
        userRepository.findByEmail(user.getEmailAddress()).ifPresent(user1 -> {
            throw new UserAlreadyExistsException(user.getEmailAddress());
        });
        return userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (!user.getEmailAddress().equals(updatedUser.getEmailAddress())) {
            throw new IllegalArgumentException("Email cannot be changed");
        }
        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }
}
