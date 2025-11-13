package com.github.mehrdadfalahati.reservation.service.dataaccess.mapper;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.ReservationEntity;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationStatus;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ReservationDataMapperTest {

    private final ReservationDataMapper mapper = Mappers.getMapper(ReservationDataMapper.class);

    @Test
    void shouldMapReservationEntityToDomain() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity entity = new ReservationEntity();
        entity.setId("01HPQR5EXAMPLE");
        entity.setUserId(1L);
        entity.setAvailableSlotId(2L);
        entity.setStatus(ReservationStatus.ACTIVE);
        entity.setReservedAt(now);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setVersion(0L);

        // When
        Reservation reservation = mapper.toDomain(entity);

        // Then
        assertNotNull(reservation);
        assertEquals(new ReservationId("01HPQR5EXAMPLE"), reservation.getId());
        assertEquals(new UserId(1L), reservation.getUserId());
        assertEquals(new AvailableSlotId(2L), reservation.getAvailableSlotId());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertEquals(now, reservation.getReservedAt());
        assertEquals(now, reservation.getCreatedAt());
        assertEquals(now, reservation.getUpdatedAt());
        assertEquals(0L, reservation.getVersion());
        assertNull(reservation.getCancelledAt());
    }

    @Test
    void shouldMapReservationDomainToEntity() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Reservation reservation = Reservation.builder()
                .id(new ReservationId("01HPQR5EXAMPLE"))
                .userId(new UserId(1L))
                .availableSlotId(new AvailableSlotId(2L))
                .status(ReservationStatus.ACTIVE)
                .reservedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .version(0L)
                .build();

        // When
        ReservationEntity entity = mapper.toEntity(reservation);

        // Then
        assertNotNull(entity);
        assertEquals("01HPQR5EXAMPLE", entity.getId());
        assertEquals(1L, entity.getUserId());
        assertEquals(2L, entity.getAvailableSlotId());
        assertEquals(ReservationStatus.ACTIVE, entity.getStatus());
        assertEquals(now, entity.getReservedAt());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
        assertEquals(0L, entity.getVersion());
        assertNull(entity.getCancelledAt());
    }

    @Test
    void shouldMapCancelledReservation() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant cancelledAt = now.plusSeconds(3600);
        ReservationEntity entity = new ReservationEntity();
        entity.setId("01HPQR5CANCELLED");
        entity.setUserId(1L);
        entity.setAvailableSlotId(2L);
        entity.setStatus(ReservationStatus.CANCELLED);
        entity.setReservedAt(now);
        entity.setCancelledAt(cancelledAt);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(cancelledAt);
        entity.setVersion(1L);

        // When
        Reservation reservation = mapper.toDomain(entity);

        // Then
        assertNotNull(reservation);
        assertEquals(new ReservationId("01HPQR5CANCELLED"), reservation.getId());
        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertEquals(cancelledAt, reservation.getCancelledAt());
        assertEquals(1L, reservation.getVersion());
        assertTrue(reservation.isCancelled());
        assertFalse(reservation.isActive());
    }

    @Test
    void shouldMapExpiredReservation() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity entity = new ReservationEntity();
        entity.setId("01HPQR5EXPIRED");
        entity.setUserId(1L);
        entity.setAvailableSlotId(2L);
        entity.setStatus(ReservationStatus.EXPIRED);
        entity.setReservedAt(now.minusSeconds(7200));
        entity.setCreatedAt(now.minusSeconds(7200));
        entity.setUpdatedAt(now);
        entity.setVersion(1L);

        // When
        Reservation reservation = mapper.toDomain(entity);

        // Then
        assertNotNull(reservation);
        assertEquals(ReservationStatus.EXPIRED, reservation.getStatus());
        assertTrue(reservation.isExpired());
        assertFalse(reservation.isActive());
    }

    @Test
    void shouldHandleNullReservationEntity() {
        // When
        Reservation reservation = mapper.toDomain(null);

        // Then
        assertNull(reservation);
    }

    @Test
    void shouldHandleNullReservationDomain() {
        // When
        ReservationEntity entity = mapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    void shouldMapReservationIdValueObject() {
        // Given
        String id = "01HPQR5TEST";

        // When
        ReservationId reservationId = mapper.mapReservationId(id);

        // Then
        assertNotNull(reservationId);
        assertEquals("01HPQR5TEST", reservationId.value());
    }

    @Test
    void shouldMapReservationIdToString() {
        // Given
        ReservationId reservationId = new ReservationId("01HPQR5TEST");

        // When
        String id = mapper.mapReservationId(reservationId);

        // Then
        assertNotNull(id);
        assertEquals("01HPQR5TEST", id);
    }

    @Test
    void shouldHandleNullReservationId() {
        // When
        ReservationId reservationId = mapper.mapReservationId((String) null);
        String id = mapper.mapReservationId((ReservationId) null);

        // Then
        assertNull(reservationId);
        assertNull(id);
    }

    @Test
    void shouldMapUserIdFromLong() {
        // Given
        Long userId = 42L;

        // When
        UserId result = mapper.mapUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(42L, result.value());
    }

    @Test
    void shouldMapUserIdToLong() {
        // Given
        UserId userId = new UserId(42L);

        // When
        Long result = mapper.mapUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(42L, result);
    }

    @Test
    void shouldMapAvailableSlotIdFromLong() {
        // Given
        Long slotId = 99L;

        // When
        AvailableSlotId result = mapper.mapAvailableSlotId(slotId);

        // Then
        assertNotNull(result);
        assertEquals(99L, result.value());
    }

    @Test
    void shouldMapAvailableSlotIdToLong() {
        // Given
        AvailableSlotId slotId = new AvailableSlotId(99L);

        // When
        Long result = mapper.mapAvailableSlotId(slotId);

        // Then
        assertNotNull(result);
        assertEquals(99L, result);
    }
}
