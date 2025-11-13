package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ReservationRepositoryImpl implements ReservationRepository {


    @Override
    public Reservation save(Reservation reservation) {
        return null;
    }

    @Override
    public Optional<Reservation> findById(ReservationId id) {
        return Optional.empty();
    }

    @Override
    public List<Reservation> findByUserId(UserId userId) {
        return List.of();
    }
}
