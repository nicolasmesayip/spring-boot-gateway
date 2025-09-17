package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.entity.AuthResponse;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.LoginCredentials;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.common.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginCredentials credentials) {
        Optional<UserAuthentication> userAuthentication = userAuthenticationRepository.findById(credentials.email());

        if (userAuthentication.isEmpty()) return ResponseEntity.notFound().build();
        UserAuthentication userAuth = userAuthentication.get();

        if (userAuth.isAccountLocked()) return ResponseEntity.status(429).build();
        if (!verifyPassword(userAuth, credentials)) {
            increaseFailedLoginAttempt(userAuth);
            return ResponseEntity.status(401).build();
        }
        updateLastLoginAt(userAuth);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.email(),
                credentials.password()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthResponse authResponse = new AuthResponse(jwtTokenUtil.generateToken(authentication), "Logged in successfully");
        return ResponseEntity.ok(authResponse);
    }

    @Override
    public ResponseEntity<AuthResponse> register(LoginCredentials credentials) {
        if (userAuthenticationRepository.existsById(credentials.email())) return ResponseEntity.status(409).build();

        UserAuthentication user = new UserAuthentication(credentials.email(), encodePassword(credentials.password()));
        userAuthenticationRepository.save(user);

        AuthResponse authResponse = new AuthResponse(null, "User registered");
        return ResponseEntity.ok(authResponse);
    }

    @Override
    public ResponseEntity<AuthResponse> resetPassword(String email) {
        if (!userAuthenticationRepository.existsById(email)) return ResponseEntity.notFound().build();
        emailVerificationServiceImpl.sendEmail(email);
        AuthResponse authResponse = new AuthResponse(null, "Email verification sent.");
        return ResponseEntity.ok(authResponse);
    }

    @Override
    public ResponseEntity<AuthResponse> verifyOTPCode(EmailVerification emailDetails) {
        if (!userAuthenticationRepository.existsById(emailDetails.getEmail())) return ResponseEntity.notFound().build();
        if (!emailVerificationServiceImpl.isOTPCodeValid(emailDetails)) return ResponseEntity.status(401).build();
        emailVerificationServiceImpl.deleteValidatedOTPCode(emailDetails);
        AuthResponse authResponseDto = new AuthResponse(null, "Email verified successfully.");
        return ResponseEntity.ok(authResponseDto);
    }

    @Override
    public ResponseEntity<AuthResponse> updatePasswordRequest(LoginCredentials credentials) {
        if (!userAuthenticationRepository.existsById(credentials.email())) return ResponseEntity.notFound().build();
        userAuthenticationRepository.updatePassword(credentials.email(), encodePassword(credentials.password()), LocalDateTime.now());
        userAuthenticationRepository.unlockAccount(credentials.email());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AuthResponse> deleteAccount(String email) {
        if (!userAuthenticationRepository.existsById(email)) return ResponseEntity.notFound().build();
        userAuthenticationRepository.deleteById(email);
        return ResponseEntity.noContent().build();
    }

    private boolean verifyPassword(UserAuthentication userAuthentication, LoginCredentials credentials) {
        return passwordEncoder.matches(credentials.password(), userAuthentication.getHashedPassword());
    }

    private void updateLastLoginAt(UserAuthentication userAuthentication) {
        userAuthentication.setLastLoginAt(LocalDateTime.now());
        userAuthenticationRepository.save(userAuthentication);
    }

    private void increaseFailedLoginAttempt(UserAuthentication userAuthentication) {
        if (userAuthentication.getFailedLoginAttempts() == userAuthentication.MAXIMUM_LOGIN_ATTEMPTS) {
            userAuthentication.setAccountLocked(true);
            resetPassword(userAuthentication.getEmail());
        } else {
            userAuthentication.setFailedLoginAttempts(userAuthentication.getFailedLoginAttempts() + 1);
        }
        userAuthenticationRepository.save(userAuthentication);
    }
}
