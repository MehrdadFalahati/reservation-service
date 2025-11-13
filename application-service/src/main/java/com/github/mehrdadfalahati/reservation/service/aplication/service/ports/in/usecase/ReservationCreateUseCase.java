package com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase;

import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import lombok.Builder;

public interface ReservationCreateUseCase {

    Reservation create(Command command);

    @Builder
    record Command() {}
}
