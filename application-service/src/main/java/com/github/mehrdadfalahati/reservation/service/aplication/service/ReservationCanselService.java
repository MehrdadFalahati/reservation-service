package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCanselUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.AvailableSlotRepository;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationCanselService implements ReservationCanselUseCase {

    private final ReservationRepository reservationRepository;
    private final AvailableSlotRepository availableSlotRepository;

    @Override
    @Transactional
    public Reservation cansel(Command command) {
        Objects.requireNonNull(command, "Command cannot be null");
        ReservationId reservationId = Objects.requireNonNull(command.reservationId(), "ReservationId is required");

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reservation %s not found".formatted(reservationId.value())
                ));

        reservation.cancel();

        AvailableSlot slot = availableSlotRepository.findById(reservation.getAvailableSlotId())
                .orElseThrow(() -> new IllegalStateException(
                        "Available slot %d not found for reservation %s"
                                .formatted(reservation.getAvailableSlotId().value(), reservationId.value())
                ));

        slot.setIsReserved(false);
        availableSlotRepository.save(slot);

        return reservationRepository.save(reservation);
    }
}
