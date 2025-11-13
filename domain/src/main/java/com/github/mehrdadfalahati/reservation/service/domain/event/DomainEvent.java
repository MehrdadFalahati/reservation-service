package com.github.mehrdadfalahati.reservation.service.domain.event;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
}
