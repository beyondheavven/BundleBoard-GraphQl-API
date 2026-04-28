package com.source.bundleboard.password.service;

import com.source.bundleboard.password.dto.PasswordActionResponse;
import com.source.bundleboard.password.dto.PasswordChangeInput;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface PasswordService {

    Mono<PasswordActionResponse> requestPasswordChange(PasswordChangeInput input, UserDetails user);


    Mono<PasswordActionResponse> confirmPasswordChange(String code, UserDetails user);

}
