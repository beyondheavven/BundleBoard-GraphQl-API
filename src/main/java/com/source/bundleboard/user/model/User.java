package com.source.bundleboard.user.model;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "users")
public class User implements UserDetails {

    @Id
    @Column("id")
    private Long id;

    @Column("username")
    private String username;

    @Column("email")
    private String email;

    @Column("password_hash")
    private String passwordHash;

    @Column("avatar_url")
    private String avatarUrl;

    @Column("roles")
    private Set<UserRole> roles;

    @Column("status")
    private UserStatus status;

    @Column("last_login_at")
    private Instant lastLoginAt;

    @Column("created_at")
    private Instant createdAt;

    @Column("is_setup_completed")
    private boolean isSetupCompleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public @Nullable String getPassword() {
        return this.passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status == UserStatus.active;
    }
}
