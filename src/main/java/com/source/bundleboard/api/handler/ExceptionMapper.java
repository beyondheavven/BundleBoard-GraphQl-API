package com.source.bundleboard.api.handler;

import com.source.bundleboard.api.exception.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionMapper {

    private static final Map<Class<? extends Throwable>, ErrorCode> MAPPER = new HashMap<>();

    static {
        MAPPER.put(UserNotFoundException.class, ErrorCode.USER_NOT_FOUND);
        MAPPER.put(CollectionNotFoundException.class, ErrorCode.COLLECTION_NOT_FOUND);
        MAPPER.put(AuthorNotFoundException.class, ErrorCode.AUTHOR_NOT_FOUND);
        MAPPER.put(ImageNotFoundException.class, ErrorCode.IMAGE_NOT_FOUND);
        MAPPER.put(MediaResourceNotFoundException.class, ErrorCode.COLLECTION_NOT_FOUND);
        MAPPER.put(IncorrectPasswordException.class, ErrorCode.UNAUTHORIZED);
        MAPPER.put(InvalidTokenException.class, ErrorCode.UNAUTHORIZED);
        MAPPER.put(UserStatusException.class, ErrorCode.FORBIDDEN);
        MAPPER.put(UserAlreadyExistsException.class, ErrorCode.USER_ALREADY_EXISTS);
        MAPPER.put(UnmatchedPasswordsException.class, ErrorCode.USER_ALREADY_EXISTS);
        MAPPER.put(DescriptionException.class, ErrorCode.INTERNAL_SERVER_ERROR);
        MAPPER.put(AccessDeniedException.class, ErrorCode.FORBIDDEN);
        MAPPER.put(AuthenticationException.class, ErrorCode.UNAUTHORIZED);
        MAPPER.put(ClientNotFoundException.class, ErrorCode.CLIENT_NOT_FOUND);
    }

    public ErrorCode getCode(Throwable ex) {
        if (ex instanceof DuplicateKeyException) {
            String msg = ex.getMessage();
            if (msg != null) {
                if (msg.contains("users_username_key")) {
                    return ErrorCode.USERNAME_ALREADY_EXISTS;
                }
                if (msg.contains("users_email_key")) {
                    return ErrorCode.EMAIL_ALREADY_EXISTS;
                }
            }
            return ErrorCode.USER_ALREADY_EXISTS;
        }
        return MAPPER.getOrDefault(ex.getClass(), ErrorCode.INTERNAL_SERVER_ERROR);
    }
}