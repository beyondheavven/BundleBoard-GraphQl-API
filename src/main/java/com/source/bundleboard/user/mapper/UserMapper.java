package com.source.bundleboard.user.mapper;

import com.source.bundleboard.user.dto.UpdateUserRoleResponse;
import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.dto.UserUpdateResponse;
import com.source.bundleboard.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "status", source = "status")
    UserResponseDto toDto(User user);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "refreshToken", source = "refreshToken")
    UserUpdateResponse toUpdateResponse(User user, String accessToken, String refreshToken);


}
