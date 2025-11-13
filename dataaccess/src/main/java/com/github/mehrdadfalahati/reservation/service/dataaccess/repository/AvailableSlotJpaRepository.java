package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.AvailableSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableSlotJpaRepository extends JpaRepository<AvailableSlotEntity, Long> {
}
