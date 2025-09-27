package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;

public interface RegistrationService {
    void register(UserCredentialsDto credentials, UserAccountDetails userAccountDetails);
}
