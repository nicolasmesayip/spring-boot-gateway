package com.nicolasmesa.springboot.authentication.service;

import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.common.dto.UserAccountDetailsDto;

public interface RegistrationService {
    void register(UserCredentialsDto credentials, UserAccountDetailsDto userAccountDetailsDto);

    void deleteAccount(UserCredentialsDto credentials);
}
