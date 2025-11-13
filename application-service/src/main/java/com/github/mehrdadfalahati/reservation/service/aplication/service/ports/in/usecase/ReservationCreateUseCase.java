package com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase;

import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import lombok.Builder;

import java.time.Instant;

public interface ReservationCreateUseCase {

    Reservation create(Command command);

    @Builder
    record Command(UserId userId, Instant requestedTime) {}
}
