package com.source.bundleboard.password.mapper;

import com.source.bundleboard.password.dto.PasswordActionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordMapper {


    PasswordActionResponse toPasswordActionResponse(Boolean aBoolean);
}
