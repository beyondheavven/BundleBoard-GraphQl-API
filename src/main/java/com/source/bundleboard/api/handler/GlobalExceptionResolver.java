package com.source.bundleboard.api.handler;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalExceptionResolver extends DataFetcherExceptionResolverAdapter {

    private final ExceptionMapper exceptionMapper;

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        ErrorCode errorCode = exceptionMapper.getCode(ex);

        if (errorCode == ErrorCode.INTERNAL_SERVER_ERROR) {
            log.error("Internal server error", ex);
        }

        Map<String, Object> extensions = new HashMap<>(Map.of(
                "code", errorCode.getCode(),
                "status", errorCode.name()
        ));

        String details = exceptionMapper.getDetails(ex);
        if (details != null && errorCode != ErrorCode.INTERNAL_SERVER_ERROR) {
            extensions.put("detail", details);
        }

        return GraphqlErrorBuilder.newError()
                .message(errorCode.getMessage())
                .errorType(mapToErrorType(errorCode))
                .extensions(extensions)
                .build();
    }

    private ErrorType mapToErrorType(ErrorCode code) {
        return switch (code.getCode()) {
            case 404 -> ErrorType.NOT_FOUND;
            case 401 -> ErrorType.UNAUTHORIZED;
            case 402, 403 -> ErrorType.FORBIDDEN;
            case 400, 409, 429 -> ErrorType.BAD_REQUEST;
            default -> ErrorType.INTERNAL_ERROR;
        };
    }
}
