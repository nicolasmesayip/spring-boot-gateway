package com.nicolasmesa.springboot.authentication.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tb_email_verification")
public class EmailVerification {

    @Id
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer verificationOtpCode;

    @Column(nullable = false)
    private LocalDateTime requestTimestamp;

    public EmailVerification() {
    }

    public EmailVerification(String email, Integer verificationOtpCode, LocalDateTime requestTimestamp) {
        this.email = email;
        this.verificationOtpCode = verificationOtpCode;
        this.requestTimestamp = requestTimestamp;
    }

    @PrePersist
    public void onCreate() {
        requestTimestamp = LocalDateTime.now();
    }
}
