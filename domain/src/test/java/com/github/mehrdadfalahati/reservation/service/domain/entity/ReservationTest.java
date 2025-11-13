package com.github.mehrdadfalahati.reservation.service.domain.entity;

import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationStatus;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Test
    void shouldCreateReservationWithValidParameters() {
        // Given
        ReservationId id = new ReservationId("01HPQR5EXAMPLE");
        UserId userId = new UserId(1L);
        AvailableSlotId slotId = new AvailableSlotId(1L);
        Instant reservedAt = Instant.now();

        // When
        Reservation reservation = Reservation.create(id, userId, slotId, reservedAt);

        // Then
        assertNotNull(reservation);
        assertEquals(id, reservation.getId());
        assertEquals(userId, reservation.getUserId());
        assertEquals(slotId, reservation.getAvailableSlotId());
        assertEquals(reservedAt, reservation.getReservedAt());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertNotNull(reservation.getCreatedAt());
        assertNotNull(reservation.getUpdatedAt());
        assertEquals(0L, reservation.getVersion());
        assertTrue(reservation.isActive());
        assertFalse(reservation.isCancelled());
        assertFalse(reservation.isExpired());
    }

    @Test
    void shouldRegisterReservationCreatedEvent() {
        // Given
        ReservationId id = new ReservationId("01HPQR5EXAMPLE");
        UserId userId = new UserId(1L);
        AvailableSlotId slotId = new AvailableSlotId(1L);
        Instant reservedAt = Instant.now();

        // When
        Reservation reservation = Reservation.create(id, userId, slotId, reservedAt);

        // Then
        assertEquals(1, reservation.getDomainEvents().size());
        assertEquals("ReservationCreatedEvent", reservation.getDomainEvents().get(0).getClass().getSimpleName());
    }

    @Test
    void shouldCancelActiveReservation() {
        // Given
        Reservation reservation = Reservation.create(
                new ReservationId("01HPQR5EXAMPLE"),
                new UserId(1L),
                new AvailableSlotId(1L),
                Instant.now()
        );
        reservation.clearDomainEvents();

        // When
        reservation.cancel();

        // Then
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertNotNull(reservation.getCancelledAt());
        assertTrue(reservation.isCancelled());
        assertFalse(reservation.isActive());
        assertEquals(1, reservation.getDomainEvents().size());
        assertEquals("ReservationCancelledEvent", reservation.getDomainEvents().get(0).getClass().getSimpleName());
    }

    @Test
    void shouldThrowExceptionWhenCancellingNonActiveReservation() {
        // Given
        Reservation reservation = Reservation.create(
                new ReservationId("01HPQR5EXAMPLE"),
                new UserId(1L),
                new AvailableSlotId(1L),
                Instant.now()
        );
        reservation.cancel();

        // When & Then
        assertThrows(IllegalStateException.class, reservation::cancel);
    }

    @Test
    void shouldMarkReservationAsExpired() {
        // Given
        Reservation reservation = Reservation.create(
                new ReservationId("01HPQR5EXAMPLE"),
                new UserId(1L),
                new AvailableSlotId(1L),
                Instant.now()
        );

        // When
        reservation.markAsExpired();

        // Then
        assertEquals(ReservationStatus.EXPIRED, reservation.getStatus());
        assertTrue(reservation.isExpired());
        assertFalse(reservation.isActive());
    }

    @Test
    void shouldThrowExceptionWhenExpiringNonActiveReservation() {
        // Given
        Reservation reservation = Reservation.create(
                new ReservationId("01HPQR5EXAMPLE"),
                new UserId(1L),
                new AvailableSlotId(1L),
                Instant.now()
        );
        reservation.cancel();

        // When & Then
        assertThrows(IllegalStateException.class, reservation::markAsExpired);
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                Reservation.create(
                        new ReservationId("01HPQR5EXAMPLE"),
                        null,
                        new AvailableSlotId(1L),
                        Instant.now()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenSlotIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                Reservation.create(
                        new ReservationId("01HPQR5EXAMPLE"),
                        new UserId(1L),
                        null,
                        Instant.now()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenReservedAtIsNull() {
        assertThrows(NullPointerException.class, () ->
                Reservation.create(
                        new ReservationId("01HPQR5EXAMPLE"),
                        new UserId(1L),
                        new AvailableSlotId(1L),
                        null
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenReservedAtIsInFuture() {
        assertThrows(IllegalArgumentException.class, () ->
                Reservation.create(
                        new ReservationId("01HPQR5EXAMPLE"),
                        new UserId(1L),
                        new AvailableSlotId(1L),
                        Instant.now().plusSeconds(3600)
                )
        );
    }

    @Test
    void shouldClearDomainEvents() {
        // Given
        Reservation reservation = Reservation.create(
                new ReservationId("01HPQR5EXAMPLE"),
                new UserId(1L),
                new AvailableSlotId(1L),
                Instant.now()
        );

        // When
        reservation.clearDomainEvents();

        // Then
        assertTrue(reservation.getDomainEvents().isEmpty());
    }

    @Test
    void shouldBeEqualBasedOnId() {
        // Given
        ReservationId id = new ReservationId("01HPQR5EXAMPLE");
        Reservation reservation1 = Reservation.create(id, new UserId(1L), new AvailableSlotId(1L), Instant.now());
        Reservation reservation2 = Reservation.create(id, new UserId(2L), new AvailableSlotId(2L), Instant.now());

        // Then
        assertEquals(reservation1, reservation2);
        assertEquals(reservation1.hashCode(), reservation2.hashCode());
    }
}
