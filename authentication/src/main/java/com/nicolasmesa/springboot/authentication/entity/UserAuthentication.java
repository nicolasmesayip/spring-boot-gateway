package com.nicolasmesa.springboot.authentication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_authentication")
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthentication {

    @Id
    @Column(nullable = false, unique = true, name = "email_address", length = 100)
    private String emailAddress;

    @Column(nullable = false, name = "password", length = 255)
    private String hashedPassword;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "is_account_locked")
    private Boolean isAccountLocked;

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Transient
    public Integer MAXIMUM_LOGIN_ATTEMPTS = 5;

    public UserAuthentication(String email, String hashedPassword) {
        this.emailAddress = email;
        this.hashedPassword = hashedPassword;
    }

    @PrePersist
    public void onCreate() {
        isAccountLocked = false;
        failedLoginAttempts = 0;
        registeredAt = LocalDateTime.now();
        passwordUpdatedAt = LocalDateTime.now();
    }
}
