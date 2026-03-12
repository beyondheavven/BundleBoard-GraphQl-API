package com.source.bundleboard.collection.mapper;

import com.source.bundleboard.collection.dto.CollectionResponseDto;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import com.source.bundleboard.collection.model.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    @Mapping(target = "authorId", source = "authorsId")
    @Mapping(target = "previewImageId", source = "previewImageId")
    CollectionResponseDto toDto(Collection collection);

    @Mapping(target = "authorsId", source = "authorId")
    @Mapping(target = "projectFileId", source = "archiveId")
    @Mapping(target = "previewImageId", source = "previewImageId")
    @Mapping(target = "id", ignore = true) // ID генерируется базой (bigserial)
    Collection toEntity(CreateNewCollectionDto dto);

    @Mapping(target = "authorsId", ignore = true)
    @Mapping(target = "projectFileId", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateCollectionDto dto, @MappingTarget Collection entity);
}
