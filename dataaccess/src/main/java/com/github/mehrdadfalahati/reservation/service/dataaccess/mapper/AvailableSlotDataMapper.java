package com.github.mehrdadfalahati.reservation.service.dataaccess.mapper;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.AvailableSlotEntity;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailableSlotDataMapper {

    AvailableSlot toDomain(AvailableSlotEntity entity);

    AvailableSlotEntity toEntity(AvailableSlot slot);

    default AvailableSlotId map(Long value) {
        return value != null ? new AvailableSlotId(value) : null;
    }

    default Long map(AvailableSlotId slotId) {
        return slotId != null ? slotId.value() : null;
    }
}
