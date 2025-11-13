package com.github.mehrdadfalahati.reservation.service.domain.valueobject;

import java.util.Objects;


public record Password(String value) {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;

    public Password {
        Objects.requireNonNull(value, "Password cannot be null");

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password must be at least %d characters long", MIN_LENGTH)
            );
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password cannot exceed %d characters", MAX_LENGTH)
            );
        }
    }

    /**
     * Validates a plain text password before hashing.
     * Should be called before creating a Password value object.
     *
     * @param plainPassword the plain text password to validate
     * @throws IllegalArgumentException if password doesn't meet requirements
     */
    public static void validatePlainPassword(String plainPassword) {
        Objects.requireNonNull(plainPassword, "Password cannot be null");

        if (plainPassword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password must be at least %d characters long", MIN_LENGTH)
            );
        }

        if (plainPassword.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Password cannot exceed %d characters", MAX_LENGTH)
            );
        }

        // Password strength requirements
        boolean hasUpperCase = plainPassword.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = plainPassword.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = plainPassword.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = plainPassword.chars().anyMatch(ch ->
                "!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(ch) >= 0
        );

        if (!hasUpperCase || !hasLowerCase || !hasDigit) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
            );
        }

        if (!hasSpecial) {
            throw new IllegalArgumentException(
                    "Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)"
            );
        }
    }
}
