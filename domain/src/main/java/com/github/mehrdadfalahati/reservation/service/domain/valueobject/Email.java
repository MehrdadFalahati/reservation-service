package com.github.mehrdadfalahati.reservation.service.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;


public record Email(String address) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final int MAX_LENGTH = 255;

    public Email {
        Objects.requireNonNull(address, "Email address cannot be null");

        address = address.trim();

        if (address.isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty");
        }

        if (address.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Email address cannot exceed %d characters", MAX_LENGTH)
            );
        }

        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException(
                    String.format("Invalid email address format: %s", address)
            );
        }
    }
}
