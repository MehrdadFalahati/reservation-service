package com.github.mehrdadfalahati.reservation.service.web.dto.request;

import java.time.Instant;

public record ReservationCreateRequest(
        Instant requestedTime
) {
}
