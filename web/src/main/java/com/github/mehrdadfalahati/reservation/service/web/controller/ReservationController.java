package com.github.mehrdadfalahati.reservation.service.web.controller;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCancelUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.security.util.SecurityContextUtil;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.ReservationCreateRequest;
import com.github.mehrdadfalahati.reservation.service.web.dto.response.ReservationResponse;
import com.github.mehrdadfalahati.reservation.service.web.mapper.ReservationApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationCreateUseCase reservationCreateUseCase;
    private final ReservationCancelUseCase reservationCancelUseCase;
    private final ReservationListUseCase reservationListUseCase;
    private final ReservationApiMapper reservationApiMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@RequestBody ReservationCreateRequest request) {
        Reservation reservation = reservationCreateUseCase.create(
                reservationApiMapper.toCommand(request)
        );
        return reservationApiMapper.toResponse(reservation);
    }

    @DeleteMapping("/{reservationId}")
    public ReservationResponse cancelReservation(@PathVariable String reservationId) {
        Reservation reservation = reservationCancelUseCase.cancel(
                reservationApiMapper.toCommand(reservationId)
        );
        return reservationApiMapper.toResponse(reservation);
    }

    @GetMapping
    public List<ReservationResponse> listReservations() {
        List<Reservation> reservations = reservationListUseCase.list(
                reservationApiMapper.toQuery(SecurityContextUtil.getId())
        );
        return reservationApiMapper.toResponseList(reservations);
    }
}
