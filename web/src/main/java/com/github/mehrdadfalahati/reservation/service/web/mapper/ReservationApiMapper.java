package com.github.mehrdadfalahati.reservation.service.web.mapper;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCancelUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.security.util.SecurityContextUtil;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.ReservationCreateRequest;
import com.github.mehrdadfalahati.reservation.service.web.dto.response.ReservationResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationApiMapper {

    ReservationResponse toResponse(Reservation reservation);

    List<ReservationResponse> toResponseList(List<Reservation> reservations);

    default ReservationCreateUseCase.Command toCommand(ReservationCreateRequest request) {
        return ReservationCreateUseCase.Command.builder()
                .userId(new UserId(SecurityContextUtil.getId()))
                .requestedTime(request.requestedTime())
                .build();
    }

    default ReservationCancelUseCase.Command toCommand(String reservationId) {
        return ReservationCancelUseCase.Command.builder()
                .reservationId(new ReservationId(reservationId))
                .build();
    }

    default ReservationListUseCase.Query toQuery(Long userId) {
        return ReservationListUseCase.Query.builder()
                .userId(new UserId(userId))
                .build();
    }

    default String map(ReservationId id) {
        return id != null ? id.value() : null;
    }

    default Long map(UserId id) {
        return id != null ? id.value() : null;
    }

    default Long map(AvailableSlotId id) {
        return id != null ? id.value() : null;
    }
}
