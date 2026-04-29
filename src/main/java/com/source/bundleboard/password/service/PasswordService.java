package com.source.bundleboard.password.service;

import com.source.bundleboard.password.dto.*;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface PasswordService {

    Mono<PasswordChangeResponse> requestPasswordChange(PasswordChangeInput input, UserDetails user);
    
    Mono<PasswordChangeResponse> confirmPasswordChange(String code, UserDetails user);

    Mono<PasswordResetResponse> requestPasswordReset(PasswordResetInput input);

    Mono<PasswordResetResponse> confirmPasswordReset(PasswordConfirmResetInput input);
}
