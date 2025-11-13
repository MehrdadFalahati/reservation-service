package com.github.mehrdadfalahati.reservation.service.dataaccess.mapper;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.AvailableSlotEntity;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class AvailableSlotDataMapperTest {

    private final AvailableSlotDataMapper mapper = Mappers.getMapper(AvailableSlotDataMapper.class);

    @Test
    void shouldMapAvailableSlotEntityToDomain() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlotEntity entity = new AvailableSlotEntity();
        entity.setId(1L);
        entity.setStartTime(now);
        entity.setEndTime(now.plusSeconds(3600));
        entity.setIsReserved(false);

        // When
        AvailableSlot slot = mapper.toDomain(entity);

        // Then
        assertNotNull(slot);
        assertEquals(new AvailableSlotId(1L), slot.getId());
        assertEquals(now, slot.getStartTime());
        assertEquals(now.plusSeconds(3600), slot.getEndTime());
        assertFalse(slot.getIsReserved());
    }

    @Test
    void shouldMapAvailableSlotDomainToEntity() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlot slot = AvailableSlot.builder()
                .id(new AvailableSlotId(1L))
                .startTime(now)
                .endTime(now.plusSeconds(3600))
                .isReserved(false)
                .build();

        // When
        AvailableSlotEntity entity = mapper.toEntity(slot);

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals(now, entity.getStartTime());
        assertEquals(now.plusSeconds(3600), entity.getEndTime());
        assertFalse(entity.getIsReserved());
    }

    @Test
    void shouldMapReservedSlot() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        AvailableSlotEntity entity = new AvailableSlotEntity();
        entity.setId(2L);
        entity.setStartTime(now);
        entity.setEndTime(now.plusSeconds(1800));
        entity.setIsReserved(true);

        // When
        AvailableSlot slot = mapper.toDomain(entity);

        // Then
        assertNotNull(slot);
        assertEquals(new AvailableSlotId(2L), slot.getId());
        assertTrue(slot.getIsReserved());
    }

    @Test
    void shouldHandleNullAvailableSlotEntity() {
        // When
        AvailableSlot slot = mapper.toDomain(null);

        // Then
        assertNull(slot);
    }

    @Test
    void shouldHandleNullAvailableSlotDomain() {
        // When
        AvailableSlotEntity entity = mapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    void shouldMapAvailableSlotIdValueObject() {
        // Given
        Long id = 456L;

        // When
        AvailableSlotId slotId = mapper.map(id);

        // Then
        assertNotNull(slotId);
        assertEquals(456L, slotId.value());
    }

    @Test
    void shouldMapAvailableSlotIdToLong() {
        // Given
        AvailableSlotId slotId = new AvailableSlotId(456L);

        // When
        Long id = mapper.map(slotId);

        // Then
        assertNotNull(id);
        assertEquals(456L, id);
    }

    @Test
    void shouldHandleNullAvailableSlotId() {
        // When
        AvailableSlotId slotId = mapper.map((Long) null);
        Long id = mapper.map((AvailableSlotId) null);

        // Then
        assertNull(slotId);
        assertNull(id);
    }
}
