package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.ReservationEntity;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationStatus;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = com.github.mehrdadfalahati.reservation.service.dataaccess.config.TestDataAccessConfiguration.class)
class ReservationJpaRepositoryTest {

    @Autowired
    private ReservationJpaRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldSaveAndRetrieveReservation() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(1L);
        reservation.setAvailableSlotId(1L);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(now);
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now);

        // When
        ReservationEntity saved = repository.save(reservation);

        // Then
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getUserId());
        assertEquals(1L, saved.getAvailableSlotId());
        assertEquals(ReservationStatus.ACTIVE, saved.getStatus());
        assertEquals(0L, saved.getVersion());
    }

    @Test
    void shouldFindReservationsByUserId() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        ReservationEntity reservation1 = new ReservationEntity();
        reservation1.setUserId(1L);
        reservation1.setAvailableSlotId(1L);
        reservation1.setStatus(ReservationStatus.ACTIVE);
        reservation1.setReservedAt(now);
        reservation1.setCreatedAt(now);
        reservation1.setUpdatedAt(now);
        repository.save(reservation1);

        ReservationEntity reservation2 = new ReservationEntity();
        reservation2.setUserId(1L);
        reservation2.setAvailableSlotId(2L);
        reservation2.setStatus(ReservationStatus.CANCELLED);
        reservation2.setReservedAt(now);
        reservation2.setCancelledAt(now.plusSeconds(3600));
        reservation2.setCreatedAt(now);
        reservation2.setUpdatedAt(now.plusSeconds(3600));
        repository.save(reservation2);

        ReservationEntity reservation3 = new ReservationEntity();
        reservation3.setUserId(2L);
        reservation3.setAvailableSlotId(3L);
        reservation3.setStatus(ReservationStatus.ACTIVE);
        reservation3.setReservedAt(now);
        reservation3.setCreatedAt(now);
        reservation3.setUpdatedAt(now);
        repository.save(reservation3);

        // When
        List<ReservationEntity> userReservations = repository.findByUserId(1L);

        // Then
        assertEquals(2, userReservations.size());
        assertTrue(userReservations.stream().allMatch(r -> r.getUserId().equals(1L)));
    }

    @Test
    void shouldUpdateReservationStatus() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(1L);
        reservation.setAvailableSlotId(1L);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(now);
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now);
        ReservationEntity saved = repository.save(reservation);
        entityManager.flush();
        entityManager.clear();

        // When
        ReservationEntity found = repository.findById(saved.getId()).orElseThrow();
        found.setStatus(ReservationStatus.CANCELLED);
        found.setCancelledAt(now.plusSeconds(3600));
        found.setUpdatedAt(now.plusSeconds(3600));
        ReservationEntity updated = repository.save(found);
        entityManager.flush();

        // Then
        assertEquals(ReservationStatus.CANCELLED, updated.getStatus());
        assertEquals(now.plusSeconds(3600), updated.getCancelledAt());
        assertEquals(1L, updated.getVersion());
    }

    @Test
    void shouldHandleOptimisticLocking() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(1L);
        reservation.setAvailableSlotId(1L);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(now);
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now);
        ReservationEntity saved = repository.save(reservation);
        entityManager.flush();
        entityManager.clear();

        // When
        ReservationEntity found = repository.findById(saved.getId()).orElseThrow();
        found.setStatus(ReservationStatus.CANCELLED);
        ReservationEntity updated = repository.save(found);
        entityManager.flush();

        // Then
        assertEquals(1L, updated.getVersion());
    }

    @Test
    void shouldFindReservationById() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(1L);
        reservation.setAvailableSlotId(1L);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setReservedAt(now);
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now);
        ReservationEntity saved = repository.save(reservation);

        // When
        Optional<ReservationEntity> found = repository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(1L, found.get().getUserId());
    }

    @Test
    void shouldReturnEmptyWhenReservationNotFound() {
        // When
        Optional<ReservationEntity> found = repository.findById("NON_EXISTENT_ID");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void shouldSaveExpiredReservation() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(1L);
        reservation.setAvailableSlotId(1L);
        reservation.setStatus(ReservationStatus.EXPIRED);
        reservation.setReservedAt(now.minusSeconds(7200));
        reservation.setCreatedAt(now.minusSeconds(7200));
        reservation.setUpdatedAt(now);

        // When
        ReservationEntity saved = repository.save(reservation);

        // Then
        assertNotNull(saved.getId());
        assertEquals(ReservationStatus.EXPIRED, saved.getStatus());
    }
}
