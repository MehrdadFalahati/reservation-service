package com.github.mehrdadfalahati.reservation.service.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    void shouldCreateValidUsername() {
        Username username = new Username("john_doe");
        assertEquals("john_doe", username.value());
    }

    @Test
    void shouldTrimUsername() {
        Username username = new Username("  john_doe  ");
        assertEquals("john_doe", username.value());
    }

    @Test
    void shouldThrowExceptionForNullUsername() {
        assertThrows(NullPointerException.class, () -> new Username(null));
    }

    @Test
    void shouldThrowExceptionForEmptyUsername() {
        assertThrows(IllegalArgumentException.class, () -> new Username(""));
        assertThrows(IllegalArgumentException.class, () -> new Username("   "));
    }

    @Test
    void shouldThrowExceptionForTooShortUsername() {
        assertThrows(IllegalArgumentException.class, () -> new Username("ab"));
    }

    @Test
    void shouldThrowExceptionForTooLongUsername() {
        String longUsername = "a".repeat(51);
        assertThrows(IllegalArgumentException.class, () -> new Username(longUsername));
    }

    @Test
    void shouldThrowExceptionForInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> new Username("user name"));
        assertThrows(IllegalArgumentException.class, () -> new Username("user@name"));
        assertThrows(IllegalArgumentException.class, () -> new Username("user.name"));
        assertThrows(IllegalArgumentException.class, () -> new Username("user#name"));
    }

    @Test
    void shouldAcceptValidUsernames() {
        assertDoesNotThrow(() -> new Username("john"));
        assertDoesNotThrow(() -> new Username("john_doe"));
        assertDoesNotThrow(() -> new Username("john-doe"));
        assertDoesNotThrow(() -> new Username("user123"));
        assertDoesNotThrow(() -> new Username("User_Name-123"));
    }
}
