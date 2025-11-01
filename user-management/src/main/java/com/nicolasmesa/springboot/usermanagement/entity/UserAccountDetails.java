package com.nicolasmesa.springboot.usermanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_users")
public class UserAccountDetails {

    @Id
    @Column(nullable = false, unique = true, length = 100, updatable = false)
    private String emailAddress;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 2)
    private String countryCode;

    @Column(nullable = false, length = 15)
    private String mobileNumber;

    @Column(nullable = false, length = 255)
    private String homeAddress;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
