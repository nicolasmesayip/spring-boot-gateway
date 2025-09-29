package com.nicolasmesa.springboot.usermanagement.mapper;

import com.nicolasmesa.springboot.usermanagement.dto.UserAccountDetailsDto;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAccountMapper {
    UserAccountMapper INSTANCE = Mappers.getMapper(UserAccountMapper.class);

    UserAccountDetails toEntity(UserAccountDetailsDto dto);

    UserAccountDetailsDto toDto(UserAccountDetails entity);
}
