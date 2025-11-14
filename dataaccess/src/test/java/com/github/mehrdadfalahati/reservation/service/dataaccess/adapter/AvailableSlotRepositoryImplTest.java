package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.AvailableSlotEntity;
import com.github.mehrdadfalahati.reservation.service.dataaccess.repository.AvailableSlotJpaRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = com.github.mehrdadfalahati.reservation.service.dataaccess.config.TestDataAccessConfiguration.class)
@ComponentScan(basePackages = "com.github.mehrdadfalahati.reservation.service.dataaccess")
class AvailableSlotRepositoryImplTest {

    @Autowired
    private AvailableSlotRepositoryImpl availableSlotRepository;

    @Autowired
    private AvailableSlotJpaRepository availableSlotJpaRepository;

    @Test
    void shouldFindAndLockNearestAvailableSlot() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        AvailableSlotEntity slot1 = new AvailableSlotEntity();
        slot1.setStartTime(now.plusSeconds(3600));
        slot1.setEndTime(now.plusSeconds(5400));
        slot1.setIsReserved(false);
        availableSlotJpaRepository.save(slot1);

        AvailableSlotEntity slot2 = new AvailableSlotEntity();
        slot2.setStartTime(now.plusSeconds(7200));
        slot2.setEndTime(now.plusSeconds(9000));
        slot2.setIsReserved(false);
        availableSlotJpaRepository.save(slot2);

        // When
        Optional<AvailableSlot> result = availableSlotRepository.findAndLockNearestAvailableSlot(now);

        // Then
        assertTrue(result.isPresent());
        AvailableSlot slot = result.get();
        assertEquals(now.plusSeconds(3600), slot.getStartTime());
        assertFalse(slot.getIsReserved());
    }

    @Test
    void shouldNotFindReservedSlots() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        AvailableSlotEntity reservedSlot = new AvailableSlotEntity();
        reservedSlot.setStartTime(now.plusSeconds(3600));
        reservedSlot.setEndTime(now.plusSeconds(5400));
        reservedSlot.setIsReserved(true);
        availableSlotJpaRepository.save(reservedSlot);

        // When
        Optional<AvailableSlot> result = availableSlotRepository.findAndLockNearestAvailableSlot(now);

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
        availableSlotJpaRepository.save(pastSlot);

        // When
        Optional<AvailableSlot> result = availableSlotRepository.findAndLockNearestAvailableSlot(now);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldSaveAvailableSlot() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlot slot = AvailableSlot.builder()
                .id(new AvailableSlotId(null))
                .startTime(now)
                .endTime(now.plusSeconds(1800))
                .isReserved(false)
                .build();

        // When
        AvailableSlot saved = availableSlotRepository.save(slot);

        // Then
        assertNotNull(saved.getId());
        assertEquals(now, saved.getStartTime());
        assertEquals(now.plusSeconds(1800), saved.getEndTime());
        assertFalse(saved.getIsReserved());
    }

    @Test
    void shouldFindSlotById() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlotEntity entity = new AvailableSlotEntity();
        entity.setStartTime(now);
        entity.setEndTime(now.plusSeconds(1800));
        entity.setIsReserved(false);
        AvailableSlotEntity saved = availableSlotJpaRepository.save(entity);

        // When
        Optional<AvailableSlot> result = availableSlotRepository.findById(new AvailableSlotId(saved.getId()));

        // Then
        assertTrue(result.isPresent());
        AvailableSlot slot = result.get();
        assertEquals(new AvailableSlotId(saved.getId()), slot.getId());
        assertEquals(now, slot.getStartTime());
        assertEquals(now.plusSeconds(1800), slot.getEndTime());
    }

    @Test
    void shouldReturnEmptyWhenSlotNotFound() {
        // When
        Optional<AvailableSlot> result = availableSlotRepository.findById(new AvailableSlotId(999111L));

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldUpdateSlotReservationStatus() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlot slot = AvailableSlot.builder()
                .id(new AvailableSlotId(null))
                .startTime(now)
                .endTime(now.plusSeconds(1800))
                .isReserved(false)
                .build();
        AvailableSlot saved = availableSlotRepository.save(slot);

        // When
        AvailableSlot updated = AvailableSlot.builder()
                .id(saved.getId())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .isReserved(true)
                .build();
        AvailableSlot result = availableSlotRepository.save(updated);

        // Then
        assertTrue(result.getIsReserved());
    }
}
