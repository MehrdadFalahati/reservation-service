package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationListService implements ReservationListUseCase {

    @Override
    public List<Reservation> list(Query query) {
        return List.of();
    }
}
