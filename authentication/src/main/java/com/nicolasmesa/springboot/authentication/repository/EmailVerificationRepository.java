package com.nicolasmesa.springboot.authentication.repository;

import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {
}
