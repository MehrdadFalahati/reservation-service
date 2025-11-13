package com.github.mehrdadfalahati.reservation.service.dataaccess.mapper;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.ReservationEntity;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationDataMapper {

    Reservation toDomain(ReservationEntity entity);

    ReservationEntity toEntity(Reservation reservation);

    default ReservationId mapReservationId(String value) {
        return value != null ? new ReservationId(value) : null;
    }

    default String mapReservationId(ReservationId reservationId) {
        return reservationId != null ? reservationId.value() : null;
    }

    default UserId mapUserId(Long value) {
        return value != null ? new UserId(value) : null;
    }

    default Long mapUserId(UserId userId) {
        return userId != null ? userId.value() : null;
    }

    default AvailableSlotId mapAvailableSlotId(Long value) {
        return value != null ? new AvailableSlotId(value) : null;
    }

    default Long mapAvailableSlotId(AvailableSlotId slotId) {
        return slotId != null ? slotId.value() : null;
    }
}
