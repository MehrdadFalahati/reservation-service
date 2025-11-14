package com.github.mehrdadfalahati.reservation.service.security.util;

import com.github.mehrdadfalahati.reservation.service.security.entity.UserSecurity;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityContextUtil {

    public static String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserSecurity)
            return ((UserSecurity) principal).getUsername();

        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static Long getId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserSecurity)
            return ((UserSecurity) principal).getId();

        return null;
    }
}
