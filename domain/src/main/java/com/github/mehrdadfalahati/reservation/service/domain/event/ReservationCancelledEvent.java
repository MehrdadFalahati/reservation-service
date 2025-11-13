package com.github.mehrdadfalahati.reservation.service.domain.event;

import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;

import java.time.Instant;

public record ReservationCancelledEvent(
        ReservationId reservationId,
        UserId userId,
        AvailableSlotId slotId,
        Instant cancelledAt,
        Instant occurredOn
) implements DomainEvent {

    public ReservationCancelledEvent(
            ReservationId reservationId,
            UserId userId,
            AvailableSlotId slotId,
            Instant cancelledAt
    ) {
        this(reservationId, userId, slotId, cancelledAt, Instant.now());
    }
}
