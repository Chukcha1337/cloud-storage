package com.chuckcha.cloudfilestorage.security.model;

import com.chuckcha.cloudfilestorage.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@ToString(exclude = "password")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserDetailsImpl implements UserDetails, CredentialsContainer {

    private final Long id;
    private final String username;
    @JsonIgnore
    private String password;
    private final List<GrantedAuthority> authorities;

    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public static UserDetailsImpl from(User user) {
        return UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(user.getRole()))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}
