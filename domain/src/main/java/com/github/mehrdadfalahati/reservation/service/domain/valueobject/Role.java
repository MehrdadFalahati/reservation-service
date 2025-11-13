package com.github.mehrdadfalahati.reservation.service.domain.valueobject;


import java.util.Set;

import static com.github.mehrdadfalahati.reservation.service.domain.valueobject.Permission.RESERVATION_READ;
import static com.github.mehrdadfalahati.reservation.service.domain.valueobject.Permission.RESERVATION_WRITE;


public enum Role {
    USER(Set.of(RESERVATION_WRITE, RESERVATION_READ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}
