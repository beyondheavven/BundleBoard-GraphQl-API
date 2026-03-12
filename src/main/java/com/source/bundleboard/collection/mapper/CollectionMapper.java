package com.source.bundleboard.collection.mapper;

import com.source.bundleboard.collection.dto.CollectionResponseDto;
import com.source.bundleboard.collection.dto.CreateNewCollectionDto;
import com.source.bundleboard.collection.model.Collection;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CollectionMapper {

    CollectionResponseDto toDto(Collection collection);

    Collection toEntity(CreateNewCollectionDto collection);

}
