package com.source.bundleboard.password.mapper;

import com.source.bundleboard.password.dto.PasswordChangeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordMapper {


    PasswordChangeResponse toPasswordActionResponse(Boolean aBoolean);
}
