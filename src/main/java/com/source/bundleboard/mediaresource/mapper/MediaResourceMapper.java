package com.source.bundleboard.mediaresource.mapper;

import com.source.bundleboard.mediaresource.dto.GetMediaResourceByIdResponse;
import com.source.bundleboard.mediaresource.model.MediaResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MediaResourceMapper {


    GetMediaResourceByIdResponse toDto(MediaResource mediaResource);
}
