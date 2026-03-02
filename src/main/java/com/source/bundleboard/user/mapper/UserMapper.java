package com.source.bundleboard.user.mapper;

import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDto(User user);

}
