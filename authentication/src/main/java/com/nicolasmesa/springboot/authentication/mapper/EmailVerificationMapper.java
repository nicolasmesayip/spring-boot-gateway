package com.nicolasmesa.springboot.authentication.mapper;

import com.nicolasmesa.springboot.authentication.dto.EmailVerificationDto;
import com.nicolasmesa.springboot.authentication.entity.EmailVerification;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EmailVerificationMapper {
    EmailVerificationMapper INSTANCE = Mappers.getMapper(EmailVerificationMapper.class);

    EmailVerification toEntity(EmailVerificationDto dto);

    EmailVerificationDto toDto(EmailVerification entity);
}
