package com.github.mehrdadfalahati.reservation.service.security.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password encoder that supports current BCrypt hashes while remaining compatible with
 * legacy plain-text style values that may still exist in seeded databases.
 */
public class LegacyCompatiblePasswordEncoder implements PasswordEncoder {

    private static final String BCRYPT_PREFIX = "$2a$";
    private static final String BCRYPT_PREFIX_ALT = "$2b$";
    private static final String BCRYPT_PREFIX_STRONG = "$2y$";

    private final PasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }

        if (isBcrypt(encodedPassword)) {
            return delegate.matches(rawPassword, encodedPassword);
        }

        // Legacy fallback: direct comparison when value is stored as plain text.
        return encodedPassword.contentEquals(rawPassword);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return encodedPassword != null && !isBcrypt(encodedPassword);
    }

    private boolean isBcrypt(String encodedPassword) {
        return encodedPassword.startsWith(BCRYPT_PREFIX)
                || encodedPassword.startsWith(BCRYPT_PREFIX_ALT)
                || encodedPassword.startsWith(BCRYPT_PREFIX_STRONG);
    }
}
