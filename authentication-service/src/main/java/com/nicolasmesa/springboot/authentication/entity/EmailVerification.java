package com.nicolasmesa.springboot.authentication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_email_verification")
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {

    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String emailAddress;

    @Column(nullable = false)
    private Integer verificationOtpCode;

    @Column(nullable = false)
    private LocalDateTime requestTimestamp;

    @PrePersist
    public void onCreate() {
        requestTimestamp = LocalDateTime.now();
    }
}
