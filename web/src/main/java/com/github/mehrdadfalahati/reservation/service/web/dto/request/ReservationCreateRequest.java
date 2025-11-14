package com.github.mehrdadfalahati.reservation.service.web.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record ReservationCreateRequest(
        @NotNull(message = "User id is required")
        Long userId,
        Instant requestedTime
) {
}
