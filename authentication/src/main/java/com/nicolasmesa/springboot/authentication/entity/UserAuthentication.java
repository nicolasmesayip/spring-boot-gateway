package com.nicolasmesa.springboot.authentication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_auth")
public class UserAuthentication {
    @Id
    @Column(nullable = false, unique = true, name = "email")
    private String email;
    @Column(nullable = false, name = "password")
    private String hashedPassword;
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;
    @Column(name = "account_locked")
    private boolean accountLocked;
    @Column(name = "password_updated_ts")
    private LocalDateTime passwordUpdatedAt;
    @Column(name = "last_login_ts")
    private LocalDateTime lastLoginAt;
    @Column(name = "register_ts")
    private LocalDateTime registeredAt;

    public int MAXIMUM_LOGIN_ATTEMPTS = 5;

    public UserAuthentication() {
    }

    public UserAuthentication(String email, String hashedPassword) {
        this.email = email;
        this.hashedPassword = hashedPassword;
    }

    public UserAuthentication(String email, String hashedPassword, LocalDateTime lastLoginAt) {
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.lastLoginAt = lastLoginAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public LocalDateTime getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordUpdatedAt(LocalDateTime passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @PrePersist
    public void onCreate() {
        accountLocked = false;
        failedLoginAttempts = 0;
        registeredAt = LocalDateTime.now();
        passwordUpdatedAt = LocalDateTime.now();
    }
}
