package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import org.springframework.stereotype.Service;

@Service
public class ReservationCreateService implements ReservationCreateUseCase {

    @Override
    public Reservation create(Command command) {
        return null;
    }
}
