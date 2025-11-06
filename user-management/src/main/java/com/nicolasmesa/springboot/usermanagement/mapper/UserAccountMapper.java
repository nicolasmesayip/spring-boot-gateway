package com.nicolasmesa.springboot.usermanagement.mapper;

import com.nicolasmesa.springboot.common.dto.UserAccountDetailsDto;
import com.nicolasmesa.springboot.usermanagement.entity.UserAccountDetails;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {
    UserAccountDetails toEntity(UserAccountDetailsDto dto);

    UserAccountDetailsDto toDto(UserAccountDetails entity);

    List<UserAccountDetailsDto> toDto(List<UserAccountDetails> entity);
}
