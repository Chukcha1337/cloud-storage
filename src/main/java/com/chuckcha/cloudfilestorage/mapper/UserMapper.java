package com.chuckcha.cloudfilestorage.mapper;

import com.chuckcha.cloudfilestorage.dto.UserCreateDto;
import com.chuckcha.cloudfilestorage.dto.UsernameResponseDto;
import com.chuckcha.cloudfilestorage.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "rawPassword", target = "password")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "id", ignore = true)
    User toEntity(UserCreateDto dto);

    UsernameResponseDto toDto(User user);
}
