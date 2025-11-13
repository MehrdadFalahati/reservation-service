package com.github.mehrdadfalahati.reservation.service.domain.event;

import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;

import java.time.Instant;

public record ReservationCreatedEvent(
        ReservationId reservationId,
        UserId userId,
        AvailableSlotId slotId,
        Instant reservedAt,
        Instant occurredOn
) implements DomainEvent {

    public ReservationCreatedEvent(
            ReservationId reservationId,
            UserId userId,
            AvailableSlotId slotId,
            Instant reservedAt
    ) {
        this(reservationId, userId, slotId, reservedAt, Instant.now());
    }
}
