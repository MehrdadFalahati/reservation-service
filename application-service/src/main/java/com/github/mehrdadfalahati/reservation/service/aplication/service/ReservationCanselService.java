package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.ReservationCanselUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import org.springframework.stereotype.Service;

@Service
public class ReservationCanselService implements ReservationCanselUseCase {

    @Override
    public Reservation cansel(Command command) {
        return null;
    }
}
