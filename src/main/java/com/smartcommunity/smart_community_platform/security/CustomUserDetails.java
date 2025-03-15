package com.smartcommunity.smart_community_platform.security;

import com.smartcommunity.smart_community_platform.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public record CustomUserDetails(User user) implements UserDetails {

    public CustomUserDetails(User user) {
        this.user = Objects.requireNonNull(user, "用户实体不能为空");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String rolePrefix = user.getRole().getCode().startsWith("ROLE_") ? "" : "ROLE_";
        return List.of(new SimpleGrantedAuthority(rolePrefix + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}

