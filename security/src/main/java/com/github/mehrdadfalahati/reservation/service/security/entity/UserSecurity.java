package com.github.mehrdadfalahati.reservation.service.security.entity;

import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class UserSecurity implements UserDetails {

    @Getter
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserSecurity(User user) {
        this.id = user.getId().value();
        this.username = user.getUsername().value();
        this.password = user.getPassword().value();
        this.authorities = getAuthorities(user.getRoles());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return true;
    }

    public Set<SimpleGrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream().flatMap(r -> getAuthority(r).stream())
                .collect(Collectors.toSet());
    }

    public Set<SimpleGrantedAuthority> getAuthority(Role role) {
        Set<SimpleGrantedAuthority> authorities = role.getPermissions()
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getPermissionName()))
                .collect(Collectors.toSet());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return authorities;
    }
}
