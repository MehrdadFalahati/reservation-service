package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.AvailableSlotRepository;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.UserRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReservationCreateService implements ReservationCreateUseCase {

    private final ReservationRepository reservationRepository;
    private final AvailableSlotRepository availableSlotRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Reservation create(Command command) {
        Objects.requireNonNull(command, "Command cannot be null");
        UserId userId = Objects.requireNonNull(command.userId(), "UserId is required");

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User %d not found".formatted(userId.value())
                ));

        Instant requestedTime = command.requestedTime() != null
                ? command.requestedTime()
                : Instant.now();

        AvailableSlot slot = availableSlotRepository
                .findAndLockNearestAvailableSlot(requestedTime)
                .orElseThrow(() -> new IllegalStateException(
                        "No available slots for %s".formatted(requestedTime)
                ));

        slot.setIsReserved(true);
        availableSlotRepository.save(slot);

        Reservation reservation = Reservation.create(
                userId,
                slot.getId(),
                Instant.now()
        );

        return reservationRepository.save(reservation);
    }
}
