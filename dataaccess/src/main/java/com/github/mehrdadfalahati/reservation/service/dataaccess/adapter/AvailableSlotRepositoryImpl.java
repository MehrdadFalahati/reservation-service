package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.AvailableSlotRepository;
import com.github.mehrdadfalahati.reservation.service.dataaccess.repository.AvailableSlotJpaRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AvailableSlotRepositoryImpl implements AvailableSlotRepository {

    private final AvailableSlotJpaRepository repository;

    @Override
    public Optional<AvailableSlot> findAndLockNearestAvailableSlot(Instant requestedTime) {
        return Optional.empty();
    }

    @Override
    public AvailableSlot save(AvailableSlot slot) {
        return null;
    }

    @Override
    public Optional<AvailableSlot> findById(AvailableSlotId id) {
        return Optional.empty();
    }
}
