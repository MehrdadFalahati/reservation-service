package com.github.mehrdadfalahati.reservation.service.web.dto.response;

public record LoginResponse(
        String token,
        Long userId,
        String username
) {
}
