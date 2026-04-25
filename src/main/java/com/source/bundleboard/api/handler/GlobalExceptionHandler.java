package com.source.bundleboard.api.handler;

import com.source.bundleboard.api.exception.*;
import com.source.bundleboard.api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(UserNotFoundException e){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("User not found.", e.getMessage())));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadCredentials(BadCredentialsException e){
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Bad credentials.", e.getMessage())));
    }

    @ExceptionHandler(IncorrectPasswordException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIncorrectPassword(IncorrectPasswordException e){
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Incorrect password.", e.getMessage())));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserAlreadyExists(UserAlreadyExistsException e){
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("User already exists.", e.getMessage())));
    }

    @ExceptionHandler(UserStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserStatus(UserStatusException e){
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("User status is not allowed.", e.getMessage())));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidToken(InvalidTokenException e){
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Invalid token.", e.getMessage())));
    }

    @ExceptionHandler(CollectionNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCollectionNotFound(CollectionNotFoundException e){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Collection not found.", e.getMessage())));
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAuthorNotFound(AuthorNotFoundException e){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Author not found.", e.getMessage())));
    }

    public Mono<ResponseEntity<ErrorResponse>> handleImageNotFound(ImageNotFoundException e){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Image not found.", e.getMessage())));
    }

    public Mono<ResponseEntity<ErrorResponse>> handleMediaResourceNotFound(MediaResourceNotFoundException e){
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Media resource not found.", e.getMessage())));
    }

    public Mono<ResponseEntity<ErrorResponse>> handleBadTemplateEmailException(BadTemplateEmailException e){
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Bad template email.", e.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleException(Exception e){
        log.error("Unexpected error: ", e);
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error.", e.getMessage())));
    }


}
