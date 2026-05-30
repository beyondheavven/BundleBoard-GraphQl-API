package com.source.bundleboard.collection.mapper;

import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.model.Collection;
import com.source.bundleboard.image.dto.ImageShortResponse;
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

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "mediaResourceId", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(UpdateCollectionDto dto, @MappingTarget Collection entity);


    @Mapping(target = "id", source = "collection.id")
    @Mapping(target = "name", source = "collection.name")
    @Mapping(target = "previewImage", source = "image")
    CollectionShortResponse mapToShortResponse(Collection collection, ImageShortResponse image);
}
