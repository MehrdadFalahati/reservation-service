package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.AvailableSlotEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface AvailableSlotJpaRepository extends JpaRepository<AvailableSlotEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT s FROM AvailableSlotEntity s " +
            "WHERE s.isReserved = :isReserved " +
            "AND s.startTime >= :requestedTime " +
            "ORDER BY s.startTime ASC " +
            "LIMIT 1")
    Optional<AvailableSlotEntity> findFirstAvailableSlotWithLock(
            @Param("requestedTime") Instant requestedTime,
            @Param("isReserved") Boolean isReserved
    );
}
