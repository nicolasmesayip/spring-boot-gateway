package com.nicolasmesa.springboot.authentication.mapper;

import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmailVerificationMapper {
    EmailVerification toEntity(EmailVerificationDto dto);

    EmailVerificationDto toDto(EmailVerification entity);
}
