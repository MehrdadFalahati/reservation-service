package com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out;

import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;

import java.time.Instant;
import java.util.Optional;

public interface AvailableSlotRepository {

    Optional<AvailableSlot> findAndLockNearestAvailableSlot(Instant requestedTime);

    AvailableSlot save(AvailableSlot slot);

    Optional<AvailableSlot> findById(AvailableSlotId id);
}
