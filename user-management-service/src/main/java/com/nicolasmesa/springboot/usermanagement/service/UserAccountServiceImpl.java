package com.nicolasmesa.springboot.usermanagement.service;

import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import com.nicolasmesa.springboot.usermanagement.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public List<UserAccountDetails> getUsers() {
        return userAccountRepository.findAll();
    }

    @Override
    public UserAccountDetails getUserByEmail(String email, String authenticatedUserEmail) {
        if (!email.equals(authenticatedUserEmail)) throw new UnAuthorizedException();
        return userAccountRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public void updateUserAccountDetails(String email, UserAccountDetails updatedUserAccountDetails) {
        UserAccountDetails userAccountDetails = userAccountRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
        userAccountDetails.setEmailAddress(email);

        userAccountRepository.save(updatedUserAccountDetails);
    }

    @Override
    public void deleteUser(String email) {
        userAccountRepository.findById(email).orElseThrow(() -> new UserNotFoundException(email));
        userAccountRepository.deleteById(email);
    }

    @Override
    public void register(UserAccountDetails user, String authenticatedUserEmail) {
        if (authenticatedUserEmail.isEmpty()) throw new UnAuthorizedException();
        if (userAccountRepository.existsById(user.getEmailAddress()))
            throw new UserAlreadyExistsException(user.getEmailAddress());

        userAccountRepository.save(user);
    }
}
