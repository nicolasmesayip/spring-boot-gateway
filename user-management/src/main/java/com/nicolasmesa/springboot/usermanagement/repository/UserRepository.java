package com.nicolasmesa.springboot.usermanagement.repository;

import com.nicolasmesa.springboot.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.emailAddress = :email")
    Optional<User> findByEmail(String email);
}
