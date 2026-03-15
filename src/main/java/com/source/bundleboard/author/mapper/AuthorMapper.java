package com.source.bundleboard.author.mapper;

import com.source.bundleboard.author.dto.AuthorResponseDto;
import com.source.bundleboard.author.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "totalSales", source = "totalSales")
    @Mapping(target = "socialLinks", source = "socialLinks")
    @Mapping(target = "stripeAccountId", source = "stripeAccountId")
    AuthorResponseDto toDto(Author author);
}
