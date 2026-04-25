package com.source.bundleboard.email.service;

import com.source.bundleboard.api.exception.InvalidEmailVerificationTokenException;
import com.source.bundleboard.api.exception.UserNotFoundException;
import com.source.bundleboard.email.mail.service.MailService;
import com.source.bundleboard.email.mapper.EmailVerificationTokenMapper;
import com.source.bundleboard.email.model.EmailVerificationToken;
import com.source.bundleboard.email.model.TokenStatus;
import com.source.bundleboard.email.model.TokenType;
import com.source.bundleboard.email.repository.EmailVerificationTokenRepository;
import com.source.bundleboard.user.model.User;
import com.source.bundleboard.user.model.UserStatus;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationTokenServiceImpl implements EmailVerificationTokenService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private final UserService userService;

    private final MailService mailService;

    private final EmailVerificationTokenMapper emailVerificationTokenMapper;

    @Override
    @Transactional
    public Mono<Boolean> verifyEmail(String tokenValue) {
        return emailVerificationTokenRepository.findByToken(tokenValue)
                .filter(token -> token.getTokenStatus() == TokenStatus.pending && token.getExpiresAt().isAfter(Instant.now()))
                .flatMap(token -> userService.getUserById(token.getUserId())
                        .flatMap(user -> processUserUpdate(user, token))
                        .flatMap(user -> {
                            token.setTokenStatus(TokenStatus.verified);
                            return emailVerificationTokenRepository.save(token);
                        })
                        .thenReturn(true))
                .switchIfEmpty(Mono.error(new InvalidEmailVerificationTokenException()));
    }

    @Override
    public Mono<Boolean> sendChangeEmailToken(String newEmail, String currentEmail) {
        return userService.getUserByEmail(currentEmail)
                .flatMap(user -> {
                    EmailVerificationToken tokenEntity = generateNewToken(user.getId(), TokenType.change_email);
                    tokenEntity.setNewEmail(newEmail);

                    return emailVerificationTokenRepository.save(tokenEntity)
                            .flatMap(token -> mailService.sendVerificationEmail(newEmail, token.getToken()))
                            .thenReturn(true);
                });
    }

    @Override
    public Mono<Boolean> resendVerificationEmail(String email) {
        return userService.getUserByEmail(email)
                .filter(user -> user.getStatus() == UserStatus.inactive)
                .flatMap(user -> {
                    EmailVerificationToken newToken = generateNewToken(user.getId(), TokenType.verify_email);

                    return emailVerificationTokenRepository.save(newToken)
                            .flatMap(token -> mailService.sendVerificationEmail(email, token.getToken()))
                            .thenReturn(true);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    private EmailVerificationToken generateNewToken(Long userId, TokenType tokenType) {
        EmailVerificationToken token = new EmailVerificationToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setTokenType(tokenType);
        token.setTokenStatus(TokenStatus.pending);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(Instant.now().plusSeconds(3600));
        return token;
    }

    private Mono<User> processUserUpdate(User user, EmailVerificationToken token) {
        if(token.getTokenType() == TokenType.change_email && token.getNewEmail() != null){
            user.setEmail(token.getNewEmail());
        }
        user.setStatus(UserStatus.active);
        return userService.saveUser(user);
    }

}
