package com.source.bundleboard.mediaresource.mapper;

import com.source.bundleboard.mediaresource.dto.MediaResourceResponseDto;
import com.source.bundleboard.mediaresource.model.MediaResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MediaResourceMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "filePath", source = "filePath")
    @Mapping(target = "mimeType", source = "mimeType")
    @Mapping(target = "provider", source = "provider")
    @Mapping(target = "fileSize", source = "fileSize")
    MediaResourceResponseDto toDto(MediaResource mediaResource);
}
