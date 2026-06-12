package com.source.bundleboard.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddCommentRequest(

        @NotNull(message = "Collection ID is required")
        Long collectionId,

        @NotBlank(message = "Comment content cannot be empty")
        @Size(max = 1000, message = "Comment content must not exceed 1000 characters")
        String content
) {
}
