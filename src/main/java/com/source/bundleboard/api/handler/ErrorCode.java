package com.source.bundleboard.api.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(404, "User not found"),
    COLLECTION_NOT_FOUND(404, "Collection not found"),
    AUTHOR_NOT_FOUND(404, "Author not found"),
    IMAGE_NOT_FOUND(404, "Image not found"),
    CLIENT_NOT_FOUND(404, "Client not found"),

    UNAUTHORIZED(401, "Authentication failed"),
    FORBIDDEN(403, "Access denied"),

    USER_ALREADY_EXISTS(409, "Conflict"),

    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    USERNAME_ALREADY_EXISTS(409, "This username is already occupied by another user"),
    EMAIL_ALREADY_EXISTS(409, "This email route is already registered in the database");
    private final int code;
    private final String message;
}
