package com.source.bundleboard.user.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {

    admin, author, client;

    public static List<SimpleGrantedAuthority> toAuthorities(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name().toUpperCase()))
                .collect(Collectors.toList());
    }

}
