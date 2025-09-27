package com.nicolasmesa.springboot.authentication.repository;

import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface UserAuthenticationRepository extends JpaRepository<UserAuthentication, String> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("Update UserAuthentication u SET u.hashedPassword =:hashedPassword,u.passwordUpdatedAt =:passwordUpdatedAt WHERE u.emailAddress =:emailAddress")
    void updatePassword(@Param("emailAddress") String emailAddress, @Param("hashedPassword") String hashedPassword, @Param("passwordUpdatedAt") LocalDateTime passwordUpdatedAt);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("Update UserAuthentication u SET u.accountLocked = false, u.failedLoginAttempts = 0 WHERE u.emailAddress =:emailAddress")
    void unlockAccount(@Param("emailAddress") String emailAddress);
}
