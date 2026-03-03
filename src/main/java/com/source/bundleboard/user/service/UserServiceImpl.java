package com.source.bundleboard.user.service;

import com.source.bundleboard.user.dto.UserResponseDto;
import com.source.bundleboard.user.mapper.UserMapper;
import com.source.bundleboard.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public Mono<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Override
    public Flux<UserResponseDto> getAllUsers() {
        return userRepository.findAll().map(userMapper::toDto);
    }


}
