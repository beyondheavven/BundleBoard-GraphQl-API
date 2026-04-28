package com.source.bundleboard.password.service;

import com.source.bundleboard.api.exception.UnmatchedPasswordsException;
import com.source.bundleboard.email.mail.service.MailService;
import com.source.bundleboard.email.service.EmailVerificationTokenService;
import com.source.bundleboard.password.dto.PasswordActionResponse;
import com.source.bundleboard.password.dto.PasswordChangeInput;
import com.source.bundleboard.password.mapper.PasswordMapper;
import com.source.bundleboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordMapper passwordMapper;

    private final UserService userService;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder;

    private final EmailVerificationTokenService tokenService;

    @Override
    public Mono<PasswordActionResponse> requestPasswordChange(PasswordChangeInput input, UserDetails user) {
        return userService.getUserByUsername(user.getUsername())
                .flatMap(user -> {
                    if(!passwordEncoder.matches(input.currentPassword(), user.getPasswordHash())) {
                        return Mono.error(new UnmatchedPasswordsException());
                    }

                    String code = String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10000));

                });
    }

    @Override
    public Mono<PasswordActionResponse> confirmPasswordChange(String code, UserDetails user) {
        return null;
    }
}
