package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.AvailableSlotRepository;
import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.AvailableSlotEntity;
import com.github.mehrdadfalahati.reservation.service.dataaccess.mapper.AvailableSlotDataMapper;
import com.github.mehrdadfalahati.reservation.service.dataaccess.repository.AvailableSlotJpaRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AvailableSlotRepositoryImpl implements AvailableSlotRepository {

    private final AvailableSlotJpaRepository availableSlotJpaRepository;
    private final AvailableSlotDataMapper availableSlotDataMapper;

    @Override
    @Transactional
    public Optional<AvailableSlot> findAndLockNearestAvailableSlot(Instant requestedTime) {
        return availableSlotJpaRepository.findFirstAvailableSlotWithLock(requestedTime, false)
                .map(availableSlotDataMapper::toDomain);
    }

    @Override
    public AvailableSlot save(AvailableSlot slot) {
        AvailableSlotEntity entity = availableSlotDataMapper.toEntity(slot);
        AvailableSlotEntity savedEntity = availableSlotJpaRepository.save(entity);
        return availableSlotDataMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AvailableSlot> findById(AvailableSlotId id) {
        return availableSlotJpaRepository.findById(id.value())
                .map(availableSlotDataMapper::toDomain);
    }
}
