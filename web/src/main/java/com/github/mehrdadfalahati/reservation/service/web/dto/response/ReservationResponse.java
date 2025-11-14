package com.github.mehrdadfalahati.reservation.service.web.dto.response;

import java.time.Instant;

public record ReservationResponse(
        String id,
        Long userId,
        Long availableSlotId,
        String status,
        Instant reservedAt,
        Instant cancelledAt,
        Instant createdAt,
        Instant updatedAt
) {
}
