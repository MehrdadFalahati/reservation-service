package com.github.mehrdadfalahati.reservation.service.domain.entity;

import com.github.mehrdadfalahati.reservation.service.domain.base.BaseEntity;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Setter
@Getter
@Builder
@BaseEntity
public class AvailableSlot {
    private AvailableSlotId id;
    private Instant startTime;
    private Instant endTime;
    private Boolean isReserved;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvailableSlot that = (AvailableSlot) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
