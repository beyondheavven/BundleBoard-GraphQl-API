package com.source.bundleboard.image.mapper;

import com.source.bundleboard.image.dto.PreviewImageResponseDto;
import com.source.bundleboard.image.model.PreviewImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PreviewImageMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "filePath", source = "filePath")
    @Mapping(target = "width", source = "width")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "fileSize", source = "fileSize")
    @Mapping(target = "mimeType", source = "mimeType")
    PreviewImageResponseDto toDto(PreviewImage previewImage);


}
