package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UnExpectedException;
import com.nicolasmesa.springboot.common.exceptions.UserAlreadyExistsException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import static com.nicolasmesa.springboot.authentication.config.PasswordConfiguration.encodePassword;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;

    @Value("${user-account.url}")
    private String userAccountUrl;

    public RegistrationServiceImpl(UserAuthenticationRepository userAuthenticationRepository, PasswordEncoder passwordEncoder, RestTemplate restTemplate) {
        this.userAuthenticationRepository = userAuthenticationRepository;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public void register(UserCredentialsDto credentials, UserAccountDetails userAccountDetails) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-GATEWAY-EMAIL", userAccountDetails.getEmailAddress());

        if (userAuthenticationRepository.existsById(credentials.emailAddress()))
            throw new UserAlreadyExistsException(credentials.emailAddress());

        try {
            UserAuthentication user = new UserAuthentication(credentials.emailAddress(), encodePassword(passwordEncoder, credentials.password()));

            userAuthenticationRepository.save(user);

            HttpEntity<UserAccountDetails> requestEntity = new HttpEntity<>(userAccountDetails, headers);
            restTemplate.exchange(userAccountUrl + "/api/users/register", HttpMethod.POST, requestEntity, Void.class);
        } catch (Exception e) {
            restTemplate.delete(userAccountUrl + "/api/users/{email}", userAccountDetails.getEmailAddress());
            userAuthenticationRepository.deleteById(credentials.emailAddress());
            throw new UnExpectedException("Rolling back User Registry: " + e.getMessage());
        }
    }

    @Override
    public void deleteAccount(UserCredentialsDto credentials) {
        UserAuthentication userAuthentication = userAuthenticationRepository.findById(credentials.emailAddress()).orElseThrow(() -> new UserNotFoundException(credentials.emailAddress()));

        if (!verifyPassword(userAuthentication, credentials)) throw new UnAuthorizedException();

        userAuthenticationRepository.deleteById(credentials.emailAddress());
        restTemplate.delete(userAccountUrl + "/api/users/{email}", credentials.emailAddress());
    }

    private boolean verifyPassword(UserAuthentication userAuthentication, UserCredentialsDto credentials) {
        return passwordEncoder.matches(credentials.password(), userAuthentication.getHashedPassword());
    }
}
