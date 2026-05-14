package com.source.bundleboard.api.handler;

import com.source.bundleboard.api.exception.UserAlreadyExistsException;
import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.api.exception.CollectionNotFoundException;
import com.source.bundleboard.api.exception.AuthorNotFoundException;
import com.source.bundleboard.api.exception.ImageNotFoundException;
import com.source.bundleboard.api.exception.MediaResourceNotFoundException;
import com.source.bundleboard.api.exception.IncorrectPasswordException;
import com.source.bundleboard.api.exception.InvalidTokenException;
import com.source.bundleboard.api.exception.UserStatusException;
import com.source.bundleboard.api.exception.UnmatchedPasswordsException;
import com.source.bundleboard.api.exception.MinimalPriceException;
import com.source.bundleboard.api.exception.DescriptionException;
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
        MAPPER.put(MinimalPriceException.class, ErrorCode.INTERNAL_SERVER_ERROR);
        MAPPER.put(DescriptionException.class, ErrorCode.INTERNAL_SERVER_ERROR);
        MAPPER.put(AccessDeniedException.class, ErrorCode.FORBIDDEN);
        MAPPER.put(AuthenticationException.class, ErrorCode.UNAUTHORIZED);
    }

    public ErrorCode getCode(Throwable ex) {
        return MAPPER.getOrDefault(ex.getClass(), ErrorCode.INTERNAL_SERVER_ERROR);
    }
}