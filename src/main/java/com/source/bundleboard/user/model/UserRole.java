package com.source.bundleboard.user.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {

    admin, author, client;

    public String getRoleName() {
        return "ROLE_" + this.name().toUpperCase();
    }

    public SimpleGrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority(getRoleName());
    }

}
