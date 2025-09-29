package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.AuthResponse;
import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.exception.AccountLockedException;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.common.JwtTokenUtil;
import com.nicolasmesa.springboot.common.exceptions.UnAuthorizedException;
import com.nicolasmesa.springboot.common.exceptions.UserNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.nicolasmesa.springboot.authentication.config.PasswordConfiguration.encodePassword;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailVerificationServiceImpl emailVerificationServiceImpl;

    public UserAuthenticationServiceImpl(PasswordEncoder passwordEncoder, UserAuthenticationRepository userAuthenticationRepository, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, EmailVerificationServiceImpl emailVerificationServiceImpl) {
        this.passwordEncoder = passwordEncoder;
        this.userAuthenticationRepository = userAuthenticationRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.emailVerificationServiceImpl = emailVerificationServiceImpl;
    }

    @Override
    public AuthResponse login(UserCredentialsDto credentials) {
        UserAuthentication userAuthentication = userAuthenticationRepository.findById(credentials.emailAddress()).orElseThrow(() -> new UserNotFoundException(credentials.emailAddress()));

        if (userAuthentication.isAccountLocked()) throw new AccountLockedException();
        if (!verifyPassword(userAuthentication, credentials)) {
            increaseFailedLoginAttempt(userAuthentication);
            throw new UnAuthorizedException();
        }

        updateLastLoginAt(userAuthentication);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.emailAddress(),
                credentials.password()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new AuthResponse(jwtTokenUtil.generateToken(authentication), "Logged in successfully");
    }

    @Override
    public AuthResponse resetPassword(String email) {
        if (!userAuthenticationRepository.existsById(email)) throw new UserNotFoundException(email);
        emailVerificationServiceImpl.sendEmail(email);
        return new AuthResponse(null, "Email verification sent.");
    }

    @Override
    public AuthResponse verifyOTPCode(EmailVerification emailDetails) {
        if (!userAuthenticationRepository.existsById(emailDetails.getEmail()))
            throw new UserNotFoundException(emailDetails.getEmail());
        if (!emailVerificationServiceImpl.isOTPCodeValid(emailDetails)) throw new UnAuthorizedException();

        emailVerificationServiceImpl.deleteValidatedOTPCode(emailDetails);
        return new AuthResponse(null, "Email verified successfully.");
    }

    @Override
    public void updatePasswordRequest(UserCredentialsDto credentials) {
        if (!userAuthenticationRepository.existsById(credentials.emailAddress()))
            throw new UserNotFoundException(credentials.emailAddress());

        userAuthenticationRepository.updatePassword(credentials.emailAddress(), encodePassword(passwordEncoder, credentials.password()), LocalDateTime.now());
        userAuthenticationRepository.unlockAccount(credentials.emailAddress());
    }

    private boolean verifyPassword(UserAuthentication userAuthentication, UserCredentialsDto credentials) {
        return passwordEncoder.matches(credentials.password(), userAuthentication.getHashedPassword());
    }

    private void updateLastLoginAt(UserAuthentication userAuthentication) {
        userAuthentication.setLastLoginAt(LocalDateTime.now());
        userAuthenticationRepository.save(userAuthentication);
    }

    private void increaseFailedLoginAttempt(UserAuthentication userAuthentication) {
        if (userAuthentication.getFailedLoginAttempts() == userAuthentication.MAXIMUM_LOGIN_ATTEMPTS) {
            userAuthentication.setAccountLocked(true);
            resetPassword(userAuthentication.getEmailAddress());
        } else {
            userAuthentication.setFailedLoginAttempts(userAuthentication.getFailedLoginAttempts() + 1);
        }
        userAuthenticationRepository.save(userAuthentication);
    }
}
