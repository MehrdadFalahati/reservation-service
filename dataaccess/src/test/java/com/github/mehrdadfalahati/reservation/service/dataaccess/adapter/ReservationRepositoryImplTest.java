package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.ReservationEntity;
import com.github.mehrdadfalahati.reservation.service.dataaccess.repository.ReservationJpaRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = com.github.mehrdadfalahati.reservation.service.dataaccess.config.TestDataAccessConfiguration.class)
@ComponentScan(basePackages = "com.github.mehrdadfalahati.reservation.service.dataaccess")
class ReservationRepositoryImplTest {

    @Autowired
    private ReservationRepositoryImpl reservationRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Test
    void shouldSaveReservation() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Reservation reservation = Reservation.create(
                new UserId(1L),
                new AvailableSlotId(1L),
                now
        );

        // When
        Reservation saved = reservationRepository.save(reservation);

        // Then
        assertNotNull(saved.getId());
        assertEquals(ReservationStatus.ACTIVE, saved.getStatus());
        assertEquals(new UserId(1L), saved.getUserId());
        assertEquals(new AvailableSlotId(1L), saved.getAvailableSlotId());
        assertEquals(now, saved.getReservedAt());
    }

    @Test
    void shouldFindReservationById() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity entity = new ReservationEntity();
        entity.setUserId(1L);
        entity.setAvailableSlotId(1L);
        entity.setStatus(ReservationStatus.ACTIVE);
        entity.setReservedAt(now);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        ReservationEntity saved = reservationJpaRepository.save(entity);

        // When
        Optional<Reservation> result = reservationRepository.findById(new ReservationId(saved.getId()));

        // Then
        assertTrue(result.isPresent());
        Reservation reservation = result.get();
        assertEquals(new ReservationId(saved.getId()), reservation.getId());
        assertEquals(ReservationStatus.ACTIVE, reservation.getStatus());
        assertEquals(new UserId(1L), reservation.getUserId());
    }

    @Test
    void shouldReturnEmptyWhenReservationNotFound() {
        // When
        Optional<Reservation> result = reservationRepository.findById(new ReservationId("NONEXISTENT"));

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindReservationsByUserId() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        ReservationEntity entity1 = new ReservationEntity();
        entity1.setUserId(1L);
        entity1.setAvailableSlotId(1L);
        entity1.setStatus(ReservationStatus.ACTIVE);
        entity1.setReservedAt(now);
        entity1.setCreatedAt(now);
        entity1.setUpdatedAt(now);
        reservationJpaRepository.save(entity1);

        ReservationEntity entity2 = new ReservationEntity();
        entity2.setUserId(1L);
        entity2.setAvailableSlotId(2L);
        entity2.setStatus(ReservationStatus.CANCELLED);
        entity2.setReservedAt(now);
        entity2.setCancelledAt(now.plusSeconds(3600));
        entity2.setCreatedAt(now);
        entity2.setUpdatedAt(now);
        reservationJpaRepository.save(entity2);

        ReservationEntity entity3 = new ReservationEntity();
        entity3.setUserId(2L);
        entity3.setAvailableSlotId(3L);
        entity3.setStatus(ReservationStatus.ACTIVE);
        entity3.setReservedAt(now);
        entity3.setCreatedAt(now);
        entity3.setUpdatedAt(now);
        reservationJpaRepository.save(entity3);

        // When
        List<Reservation> userReservations = reservationRepository.findByUserId(new UserId(1L));

        // Then
        assertEquals(2, userReservations.size());
        assertTrue(userReservations.stream().allMatch(r -> r.getUserId().equals(new UserId(1L))));
    }

    @Test
    void shouldSaveAndRetrieveCancelledReservation() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Reservation reservation = Reservation.create(
                new UserId(1L),
                new AvailableSlotId(1L),
                now
        );
        Reservation saved = reservationRepository.save(reservation);

        // Cancel the reservation
        saved.cancel();

        // When
        Reservation updated = reservationRepository.save(saved);

        // Then
        assertEquals(ReservationStatus.CANCELLED, updated.getStatus());
        assertNotNull(updated.getCancelledAt());
    }

    @Test
    void shouldReturnEmptyListWhenNoReservationsForUser() {
        // When
        List<Reservation> reservations = reservationRepository.findByUserId(new UserId(999L));

        // Then
        assertTrue(reservations.isEmpty());
    }
}
