package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.entity.AuthResponse;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import com.nicolasmesa.springboot.authentication.entity.LoginCredentials;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import com.nicolasmesa.springboot.authentication.repository.UserAuthenticationRepository;
import com.nicolasmesa.springboot.common.JwtTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@AllArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserAuthenticationRepository userAuthenticationRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private EmailVerificationServiceImpl emailVerificationServiceImpl;

    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginCredentials credentials) {
        Optional<UserAuthentication> userAuthentication = userAuthenticationRepository.findById(credentials.getEmail());

        if (userAuthentication.isEmpty()) return ResponseEntity.notFound().build();
        UserAuthentication userAuth = userAuthentication.get();

        if (userAuth.isAccountLocked()) return ResponseEntity.status(429).build();
        if (!verifyPassword(userAuth, credentials)) {
            increaseFailedLoginAttempt(userAuth);
            return ResponseEntity.status(401).build();
        }
        updateLastLoginAt(userAuth);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                credentials.getEmail(),
                credentials.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AuthResponse authResponse = new AuthResponse(jwtTokenUtil.generateToken(authentication), "Logged in successfully");
        return ResponseEntity.ok(authResponse);
    }

    @Override
    public ResponseEntity<AuthResponse> register(LoginCredentials credentials) {
        if (userAuthenticationRepository.existsById(credentials.getEmail())) return ResponseEntity.status(409).build();
        credentials.setPassword(encodePassword(credentials.getPassword()));

        UserAuthentication user = new UserAuthentication(credentials.getEmail(), credentials.getPassword());
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
        if (!userAuthenticationRepository.existsById(credentials.getEmail())) return ResponseEntity.notFound().build();
        userAuthenticationRepository.updatePassword(credentials.getEmail(), encodePassword(credentials.getPassword()), LocalDateTime.now());
        userAuthenticationRepository.unlockAccount(credentials.getEmail());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<AuthResponse> deleteAccount(String email) {
        if (!userAuthenticationRepository.existsById(email)) return ResponseEntity.notFound().build();
        userAuthenticationRepository.deleteById(email);
        return ResponseEntity.noContent().build();
    }

    private boolean verifyPassword(UserAuthentication userAuthentication, LoginCredentials credentials) {
        return passwordEncoder.matches(credentials.getPassword(), userAuthentication.getHashedPassword());
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
