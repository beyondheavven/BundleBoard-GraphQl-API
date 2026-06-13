package com.source.bundleboard.comments.dto;

import com.source.bundleboard.comments.model.Comment;

public record CommentResponse(

        Comment comment,

        Boolean success,

        String message
) {
}
