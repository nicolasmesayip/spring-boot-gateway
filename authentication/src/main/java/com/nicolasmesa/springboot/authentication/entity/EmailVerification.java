package com.nicolasmesa.springboot.authentication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    private int verificationOtpCode;

    @Column(nullable = false)
    private LocalDateTime requestTimestamp;

    public EmailVerification() {
    }

    public EmailVerification(String email, int verificationOtpCode, LocalDateTime requestTimestamp) {
        this.email = email;
        this.verificationOtpCode = verificationOtpCode;
        this.requestTimestamp = requestTimestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getVerificationOtpCode() {
        return verificationOtpCode;
    }

    public void setVerificationOtpCode(int verificationOtpCode) {
        this.verificationOtpCode = verificationOtpCode;
    }

    public LocalDateTime getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(LocalDateTime requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }
}
