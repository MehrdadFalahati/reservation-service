package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.AvailableSlotEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = com.github.mehrdadfalahati.reservation.service.dataaccess.config.TestDataAccessConfiguration.class)
class AvailableSlotJpaRepositoryTest {

    @Autowired
    private AvailableSlotJpaRepository repository;

    @Test
    void shouldFindFirstAvailableSlotWithLock() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        AvailableSlotEntity slot1 = new AvailableSlotEntity();
        slot1.setStartTime(now.plusSeconds(3600));
        slot1.setEndTime(now.plusSeconds(5400));
        slot1.setIsReserved(false);
        repository.save(slot1);

        AvailableSlotEntity slot2 = new AvailableSlotEntity();
        slot2.setStartTime(now.plusSeconds(7200));
        slot2.setEndTime(now.plusSeconds(9000));
        slot2.setIsReserved(false);
        repository.save(slot2);

        // When
        Optional<AvailableSlotEntity> result = repository.findFirstAvailableSlotWithLock(now, false);

        // Then
        assertTrue(result.isPresent());
        assertEquals(slot1.getStartTime(), result.get().getStartTime());
    }

    @Test
    void shouldNotFindReservedSlots() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        AvailableSlotEntity reservedSlot = new AvailableSlotEntity();
        reservedSlot.setStartTime(now.plusSeconds(3600));
        reservedSlot.setEndTime(now.plusSeconds(5400));
        reservedSlot.setIsReserved(true);
        repository.save(reservedSlot);

        // When
        Optional<AvailableSlotEntity> result = repository.findFirstAvailableSlotWithLock(now, false);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotFindPastSlots() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        AvailableSlotEntity pastSlot = new AvailableSlotEntity();
        pastSlot.setStartTime(now.minusSeconds(7200));
        pastSlot.setEndTime(now.minusSeconds(5400));
        pastSlot.setIsReserved(false);
        repository.save(pastSlot);

        // When
        Optional<AvailableSlotEntity> result = repository.findFirstAvailableSlotWithLock(now, false);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindNearestSlotWhenMultipleAvailable() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        AvailableSlotEntity farSlot = new AvailableSlotEntity();
        farSlot.setStartTime(now.plusSeconds(10800));
        farSlot.setEndTime(now.plusSeconds(12600));
        farSlot.setIsReserved(false);
        repository.save(farSlot);

        AvailableSlotEntity nearSlot = new AvailableSlotEntity();
        nearSlot.setStartTime(now.plusSeconds(3600));
        nearSlot.setEndTime(now.plusSeconds(5400));
        nearSlot.setIsReserved(false);
        repository.save(nearSlot);

        AvailableSlotEntity middleSlot = new AvailableSlotEntity();
        middleSlot.setStartTime(now.plusSeconds(7200));
        middleSlot.setEndTime(now.plusSeconds(9000));
        middleSlot.setIsReserved(false);
        repository.save(middleSlot);

        // When
        Optional<AvailableSlotEntity> result = repository.findFirstAvailableSlotWithLock(now, false);

        // Then
        assertTrue(result.isPresent());
        assertEquals(nearSlot.getStartTime(), result.get().getStartTime());
    }

    @Test
    void shouldSaveAndRetrieveAvailableSlot() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlotEntity slot = new AvailableSlotEntity();
        slot.setStartTime(now);
        slot.setEndTime(now.plusSeconds(1800));
        slot.setIsReserved(false);

        // When
        AvailableSlotEntity saved = repository.save(slot);

        // Then
        assertNotNull(saved.getId());
        assertEquals(now, saved.getStartTime());
        assertEquals(now.plusSeconds(1800), saved.getEndTime());
        assertFalse(saved.getIsReserved());
    }

    @Test
    void shouldUpdateSlotReservationStatus() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlotEntity slot = new AvailableSlotEntity();
        slot.setStartTime(now);
        slot.setEndTime(now.plusSeconds(1800));
        slot.setIsReserved(false);
        AvailableSlotEntity saved = repository.save(slot);

        // When
        saved.setIsReserved(true);
        AvailableSlotEntity updated = repository.save(saved);

        // Then
        assertTrue(updated.getIsReserved());
    }
}
