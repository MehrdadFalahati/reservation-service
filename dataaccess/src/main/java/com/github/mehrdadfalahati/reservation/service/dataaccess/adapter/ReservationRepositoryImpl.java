package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.ReservationEntity;
import com.github.mehrdadfalahati.reservation.service.dataaccess.mapper.ReservationDataMapper;
import com.github.mehrdadfalahati.reservation.service.dataaccess.repository.ReservationJpaRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationDataMapper reservationDataMapper;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = reservationDataMapper.toEntity(reservation);
        ReservationEntity savedEntity = reservationJpaRepository.save(entity);
        return reservationDataMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Reservation> findById(ReservationId id) {
        return reservationJpaRepository.findById(id.value())
                .map(reservationDataMapper::toDomain);
    }

    @Override
    public List<Reservation> findByUserId(UserId userId) {
        return reservationJpaRepository.findByUserId(userId.value())
                .stream()
                .map(reservationDataMapper::toDomain)
                .toList();
    }
}
