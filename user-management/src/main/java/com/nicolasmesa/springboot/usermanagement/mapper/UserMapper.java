package com.nicolasmesa.springboot.usermanagement.mapper;

import com.nicolasmesa.springboot.usermanagement.dto.UserDto;
import com.nicolasmesa.springboot.usermanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(UserDto dto);

    UserDto toDto(User entity);
}
