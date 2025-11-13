package com.github.mehrdadfalahati.reservation.service.domain.valueobject;

public enum Permission {
    RESERVATION_WRITE("reservation:write"),
    RESERVATION_READ("reservation:read");

    private final String permissionName;

    Permission(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionName() {
        return permissionName;
    }
}
