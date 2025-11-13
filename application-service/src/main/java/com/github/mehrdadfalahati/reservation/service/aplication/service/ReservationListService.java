package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationListService implements ReservationListUseCase {

    private final ReservationRepository reservationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> list(Query query) {
        Objects.requireNonNull(query, "Query cannot be null");
        UserId userId = Objects.requireNonNull(query.userId(), "UserId is required");
        return reservationRepository.findByUserId(userId);
    }
}
