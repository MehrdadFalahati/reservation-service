package com.github.mehrdadfalahati.reservation.service.domain.entity;

import com.github.mehrdadfalahati.reservation.service.domain.base.BaseEntity;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@Builder
@BaseEntity
public class User {
    private UserId id;
    private Username username;
    private Email email;
    private Password password;
    @Builder.Default
    private Set<Role> roles = Set.of(Role.USER);
    private Instant createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
