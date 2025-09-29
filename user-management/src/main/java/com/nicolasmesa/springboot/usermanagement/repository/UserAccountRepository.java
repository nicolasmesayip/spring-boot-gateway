package com.nicolasmesa.springboot.usermanagement.repository;

import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccountDetails, String> {
}
