package com.nicolasmesa.springboot.authentication.mapper;

import com.nicolasmesa.springboot.authentication.dto.UserCredentialsDto;
import com.nicolasmesa.springboot.authentication.entity.UserAuthentication;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAuthenticationMapper {
    UserAuthenticationMapper INSTANCE = Mappers.getMapper(UserAuthenticationMapper.class);

    UserCredentialsDto credentialsToDto(UserAuthentication userAuthentication);

    UserAuthentication credentialsToEntity(UserCredentialsDto userCredentials);
}
