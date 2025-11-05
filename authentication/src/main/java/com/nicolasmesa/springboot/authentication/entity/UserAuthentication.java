package com.nicolasmesa.springboot.authentication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_auth")
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthentication {

    @Id
    @Column(nullable = false, unique = true, name = "emailAddress", length = 100)
    private String emailAddress;

    @Column(nullable = false, name = "password", length = 55)
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

    public UserAuthentication(String email, String hashedPassword) {
        this.emailAddress = email;
        this.hashedPassword = hashedPassword;
    }

    @PrePersist
    public void onCreate() {
        accountLocked = false;
        failedLoginAttempts = 0;
        registeredAt = LocalDateTime.now();
        passwordUpdatedAt = LocalDateTime.now();
    }
}
