package com.source.bundleboard.author.mapper;

import com.source.bundleboard.author.dto.AuthorResponseDto;
import com.source.bundleboard.author.model.Author;
import io.r2dbc.postgresql.codec.Json;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "totalSales", source = "totalSales")
    @Mapping(target = "socialLinks", expression = "java(mapJsonToString(author.socialLinks()))")
    @Mapping(target = "stripeAccountId", source = "stripeAccountId")
    AuthorResponseDto toDto(Author author);

    default String mapJsonToString(Json json) {
        return json != null ? json.asString() : "";
    }
}
