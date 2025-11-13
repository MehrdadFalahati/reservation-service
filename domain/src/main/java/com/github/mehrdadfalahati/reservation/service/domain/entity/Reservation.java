package com.github.mehrdadfalahati.reservation.service.domain.entity;

import com.github.mehrdadfalahati.reservation.service.domain.base.AggregateRoot;
import com.github.mehrdadfalahati.reservation.service.domain.event.DomainEvent;
import com.github.mehrdadfalahati.reservation.service.domain.event.ReservationCancelledEvent;
import com.github.mehrdadfalahati.reservation.service.domain.event.ReservationCreatedEvent;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationStatus;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
@AggregateRoot
public class Reservation {
    private ReservationId id;
    private UserId userId;
    private AvailableSlotId availableSlotId;
    private ReservationStatus status;
    private Instant reservedAt;
    private Instant cancelledAt;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    @Builder.Default
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public static Reservation create(
            UserId userId,
            AvailableSlotId availableSlotId,
            Instant reservedAt
    ) {
       return create(null, userId, availableSlotId, reservedAt);
    }


    public static Reservation create(
            ReservationId id,
            UserId userId,
            AvailableSlotId availableSlotId,
            Instant reservedAt
    ) {
        validateCreationParameters(userId, availableSlotId, reservedAt);

        Reservation reservation = Reservation.builder()
                .id(id)
                .userId(userId)
                .availableSlotId(availableSlotId)
                .status(ReservationStatus.ACTIVE)
                .reservedAt(reservedAt)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .version(0L)
                .build();

        reservation.registerEvent(new ReservationCreatedEvent(
                id,
                userId,
                availableSlotId,
                reservedAt
        ));

        return reservation;
    }

    public void cancel() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException(
                    String.format("Cannot cancel reservation %s with status %s. Only ACTIVE reservations can be cancelled.",
                            id.value(), status)
            );
        }

        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = Instant.now();
        this.updatedAt = Instant.now();

        registerEvent(new ReservationCancelledEvent(
                this.id,
                this.userId,
                this.availableSlotId,
                this.cancelledAt
        ));
    }

    public void markAsExpired() {
        if (this.status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException(
                    String.format("Cannot expire reservation %s with status %s. Only ACTIVE reservations can expire.",
                            id.value(), status)
            );
        }

        this.status = ReservationStatus.EXPIRED;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return this.status == ReservationStatus.ACTIVE;
    }

    public boolean isCancelled() {
        return this.status == ReservationStatus.CANCELLED;
    }

    public boolean isExpired() {
        return this.status == ReservationStatus.EXPIRED;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    private void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    private static void validateCreationParameters(
            UserId userId,
            AvailableSlotId availableSlotId,
            Instant reservedAt
    ) {
        Objects.requireNonNull(userId, "UserId cannot be null");
        Objects.requireNonNull(availableSlotId, "AvailableSlotId cannot be null");
        Objects.requireNonNull(reservedAt, "ReservedAt cannot be null");

        if (reservedAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("ReservedAt cannot be in the future");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", availableSlotId=" + availableSlotId +
                ", status=" + status +
                ", reservedAt=" + reservedAt +
                ", cancelledAt=" + cancelledAt +
                ", version=" + version +
                '}';
    }
}
