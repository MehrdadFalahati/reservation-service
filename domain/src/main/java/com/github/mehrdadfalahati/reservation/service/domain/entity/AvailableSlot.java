package com.github.mehrdadfalahati.reservation.service.domain.entity;

import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;

import java.time.Instant;

public class AvailableSlot {
    private AvailableSlotId availableSlot;
    private Instant startTime;
    private Instant endTime;
    private Boolean isReserved;
}
