package com.github.mehrdadfalahati.reservation.service.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

    @Test
    void shouldCreateValidHashedPassword() {
        // This represents a hashed password
        String hashedPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        Password password = new Password(hashedPassword);
        assertEquals(hashedPassword, password.value());
    }

    @Test
    void shouldThrowExceptionForNullPassword() {
        assertThrows(NullPointerException.class, () -> new Password(null));
    }

    @Test
    void shouldThrowExceptionForEmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Password(""));
    }

    @Test
    void shouldThrowExceptionForTooShortPassword() {
        assertThrows(IllegalArgumentException.class, () -> new Password("1234567"));
    }

    @Test
    void shouldThrowExceptionForTooLongPassword() {
        String longPassword = "a".repeat(101);
        assertThrows(IllegalArgumentException.class, () -> new Password(longPassword));
    }

    @Test
    void shouldAcceptValidMinimumLengthPassword() {
        assertDoesNotThrow(() -> new Password("12345678"));
    }

    @Test
    void shouldValidatePlainPasswordWithAllRequirements() {
        // Valid password with uppercase, lowercase, digit, and special char
        assertDoesNotThrow(() -> Password.validatePlainPassword("Password123!"));
    }

    @Test
    void shouldThrowExceptionForPlainPasswordWithoutUppercase() {
        assertThrows(IllegalArgumentException.class,
                () -> Password.validatePlainPassword("password123!"));
    }

    @Test
    void shouldThrowExceptionForPlainPasswordWithoutLowercase() {
        assertThrows(IllegalArgumentException.class,
                () -> Password.validatePlainPassword("PASSWORD123!"));
    }

    @Test
    void shouldThrowExceptionForPlainPasswordWithoutDigit() {
        assertThrows(IllegalArgumentException.class,
                () -> Password.validatePlainPassword("Password!"));
    }

    @Test
    void shouldThrowExceptionForPlainPasswordWithoutSpecialChar() {
        assertThrows(IllegalArgumentException.class,
                () -> Password.validatePlainPassword("Password123"));
    }

    @Test
    void shouldThrowExceptionForPlainPasswordTooShort() {
        assertThrows(IllegalArgumentException.class,
                () -> Password.validatePlainPassword("Pass1!"));
    }

    @Test
    void shouldAcceptVariousSpecialCharacters() {
        assertDoesNotThrow(() -> Password.validatePlainPassword("Password123!"));
        assertDoesNotThrow(() -> Password.validatePlainPassword("Password123@"));
        assertDoesNotThrow(() -> Password.validatePlainPassword("Password123#"));
        assertDoesNotThrow(() -> Password.validatePlainPassword("Password123$"));
        assertDoesNotThrow(() -> Password.validatePlainPassword("Password123%"));
    }
}
