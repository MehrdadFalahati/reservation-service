package com.github.mehrdadfalahati.reservation.service.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        Email email = new Email("test@example.com");
        assertEquals("test@example.com", email.address());
    }

    @Test
    void shouldTrimEmailAddress() {
        Email email = new Email("  test@example.com  ");
        assertEquals("test@example.com", email.address());
    }

    @Test
    void shouldThrowExceptionForNullEmail() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    void shouldThrowExceptionForEmptyEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Email(""));
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    void shouldThrowExceptionForInvalidEmailFormat() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> new Email("@example.com"));
        assertThrows(IllegalArgumentException.class, () -> new Email("test@"));
        assertThrows(IllegalArgumentException.class, () -> new Email("test @example.com"));
    }

    @Test
    void shouldThrowExceptionForTooLongEmail() {
        String longEmail = "a".repeat(250) + "@example.com";
        assertThrows(IllegalArgumentException.class, () -> new Email(longEmail));
    }

    @Test
    void shouldAcceptValidEmailFormats() {
        assertDoesNotThrow(() -> new Email("simple@example.com"));
        assertDoesNotThrow(() -> new Email("user.name@example.com"));
        assertDoesNotThrow(() -> new Email("user+tag@example.co.uk"));
        assertDoesNotThrow(() -> new Email("user_name@example-domain.com"));
    }
}
