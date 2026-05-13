package com.source.bundleboard.collection.mapper;

import com.source.bundleboard.collection.dto.CollectionResponse;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.dto.GetCollectionByIdResponse;
import com.source.bundleboard.collection.dto.UpdateCollectionDto;
import com.source.bundleboard.collection.model.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "previewImageId", source = "previewImageId")
    CollectionResponse toDto(Collection collection);

    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "previewImageId", source = "previewImageId")
    GetCollectionByIdResponse toGetDto(Collection collection);

    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "mediaResourceId", source = "mediaResourceId")
    @Mapping(target = "previewImageId", source = "previewImageId")
    @Mapping(target = "id", ignore = true)
    Collection toEntity(CreateNewCollectionDto dto);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "mediaResourceId", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateCollectionDto dto, @MappingTarget Collection entity);
}
