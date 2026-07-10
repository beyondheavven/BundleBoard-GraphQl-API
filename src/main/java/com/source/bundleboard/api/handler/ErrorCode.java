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
    TAG_NOT_FOUND(404, "Tag not found"),
    MEDIA_RESOURCE_NOT_FOUND(404, "Media resource not found"),
    PURCHASE_NOT_FOUND(404, "Purchase not found"),

    VALIDATION_ERROR(400, "Invalid request"),
    INVALID_FILE_TYPE(400, "Invalid file type"),
    FILE_TOO_LARGE(400, "File too large"),

    UNAUTHORIZED(401, "Authentication failed"),

    PAYMENT_REQUIRED(402, "Payment required"),
    PAYMENT_FAILED(402, "Payment failed"),
    STRIPE_ACCOUNT_NOT_FOUND(403, "Stripe account not found"),
    FORBIDDEN(403, "Access denied"),

    DUPLICATE_LIKE(409, "You have already liked this item"),
    SLUG_ALREADY_EXISTS(409, "Slug already exists"),
    USER_ALREADY_EXISTS(409, "Conflict"),
    USERNAME_ALREADY_EXISTS(409, "This username is already occupied by another user"),
    EMAIL_ALREADY_EXISTS(409, "This email is already registered in the database"),

    RATE_LIMIT_EXCEEDED(429, "Rate limit exceeded"),

    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    STORAGE_SERVICE_UNAVAILABLE(503, "Storage service unavailable"),
    EXTERNAL_SERVICE_UNAVAILABLE(503, "External service unavailable");

    private final int code;
    private final String message;
}
