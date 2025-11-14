package com.github.mehrdadfalahati.reservation.service.web.controller;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCancelUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.ReservationCreateRequest;
import com.github.mehrdadfalahati.reservation.service.web.dto.response.ReservationResponse;
import com.github.mehrdadfalahati.reservation.service.web.mapper.ReservationApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public ReservationResponse createReservation(@Valid @RequestBody ReservationCreateRequest request) {
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
    public List<ReservationResponse> listReservations(@RequestParam Long userId) {
        List<Reservation> reservations = reservationListUseCase.list(
                reservationApiMapper.toQuery(userId)
        );
        return reservationApiMapper.toResponseList(reservations);
    }
}
