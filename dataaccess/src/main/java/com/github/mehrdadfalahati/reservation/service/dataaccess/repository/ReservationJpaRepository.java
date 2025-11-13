package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, String> {
}
