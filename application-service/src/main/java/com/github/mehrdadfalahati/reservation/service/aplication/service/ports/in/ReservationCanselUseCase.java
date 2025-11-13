package com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in;

import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import lombok.Builder;

public interface ReservationCanselUseCase {

    Reservation cansel(Command command);

    @Builder
    record Command() {}
}
