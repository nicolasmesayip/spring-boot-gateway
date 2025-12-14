package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.common.dto.UserAccountDetailsDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-management-service", url = "${spring.services.user-management-service.url}")
public interface UserManagementRegistrationService {

    @PostMapping("/api/users/register")
    void register(@Valid @RequestBody UserAccountDetailsDto user, @RequestHeader("X-GATEWAY-EMAIL") String authenticatedUserEmail);

    @DeleteMapping("/api/users/{email}")
    void deleteUser(@PathVariable String email);
}
