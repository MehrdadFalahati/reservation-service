package com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase;

import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import lombok.Builder;

public interface ReservationCancelUseCase {

    Reservation cancel(Command command);

    @Builder
    record Command(ReservationId reservationId) {}
}
