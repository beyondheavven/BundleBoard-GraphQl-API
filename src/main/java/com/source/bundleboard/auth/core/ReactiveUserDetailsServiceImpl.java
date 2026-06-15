package com.source.bundleboard.auth.core;

import com.source.bundleboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)))
                .cast(UserDetails.class);
    }

    public Mono<UserDetails> loadUserById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new UsernameNotFoundException("User node not found by ID: " + id)))
                .cast(UserDetails.class);
    }
}
