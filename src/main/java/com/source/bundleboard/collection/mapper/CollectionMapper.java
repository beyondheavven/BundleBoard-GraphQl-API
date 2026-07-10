package com.source.bundleboard.collection.mapper;

import com.source.bundleboard.collection.dto.*;
import com.source.bundleboard.collection.model.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "isLiked", ignore = true)
    @Mapping(target = "galleryImages", ignore = true)
    CollectionResponse toDto(Collection collection);

    @Mapping(target = "authorId", source = "authorId")
    GetCollectionByIdResponse toGetDto(Collection collection);

    @Mapping(target = "authorId", source = "authorId")
    GetCollectionBySlugResponse toSlugDto(Collection collection);

    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "mediaResourceId", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "videoTutorialUrl", ignore = true)
    void updateEntityFromDto(UpdateCollectionRequest dto, @MappingTarget Collection entity);
}
