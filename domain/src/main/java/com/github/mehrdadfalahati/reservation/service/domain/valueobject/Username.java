package com.github.mehrdadfalahati.reservation.service.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;


public record Username(String value) {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;

    public Username {
        Objects.requireNonNull(value, "Username cannot be null");

        value = value.trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Username must be at least %d characters long", MIN_LENGTH)
            );
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Username cannot exceed %d characters", MAX_LENGTH)
            );
        }

        if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Username can only contain letters, numbers, underscores, and hyphens"
            );
        }
    }
}
