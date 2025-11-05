package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UnExpectedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import com.nicolasmesa.springboot.usermanagement.mapper.UserAccountMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.nicolasmesa.springboot.authentication.config.PasswordConfiguration.encodePassword;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserManagementRegistrationService userManagementRegistrationService;
    private final UserAccountMapper userAccountMapper;

    public RegistrationServiceImpl(UserAuthenticationRepository userAuthenticationRepository, PasswordEncoder passwordEncoder, UserManagementRegistrationService userManagementRegistrationService, UserAccountMapper userAccountMapper) {
        this.userAuthenticationRepository = userAuthenticationRepository;
        this.passwordEncoder = passwordEncoder;
        this.userManagementRegistrationService = userManagementRegistrationService;
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    @Transactional
    public void register(UserCredentialsDto credentials, UserAccountDetails userAccountDetails) {
        if (userAuthenticationRepository.existsById(credentials.emailAddress()))
            throw new UserAlreadyExistsException(credentials.emailAddress());

        try {
            UserAuthentication user = new UserAuthentication(credentials.emailAddress(), encodePassword(passwordEncoder, credentials.password()));

            userAuthenticationRepository.save(user);
            userManagementRegistrationService.register(userAccountMapper.toDto(userAccountDetails), userAccountDetails.getEmailAddress());
        } catch (Exception e) {
            userManagementRegistrationService.deleteUser(userAccountDetails.getEmailAddress());
            userAuthenticationRepository.deleteById(credentials.emailAddress());
            throw new UnExpectedException("Rolling back User Registry: " + e.getMessage());
        }
    }

    @Override
    public void deleteAccount(UserCredentialsDto credentials) {
        UserAuthentication userAuthentication = userAuthenticationRepository.findById(credentials.emailAddress()).orElseThrow(() -> new UserNotFoundException(credentials.emailAddress()));

        if (!verifyPassword(userAuthentication, credentials)) throw new UnAuthorizedException();

        userAuthenticationRepository.deleteById(credentials.emailAddress());
        userManagementRegistrationService.deleteUser(credentials.emailAddress());
    }

    private boolean verifyPassword(UserAuthentication userAuthentication, UserCredentialsDto credentials) {
        return passwordEncoder.matches(credentials.password(), userAuthentication.getHashedPassword());
    }
}
