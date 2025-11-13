package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationListServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    private ReservationListService reservationListService;

    @BeforeEach
    void setUp() {
        reservationListService = new ReservationListService(reservationRepository);
    }

    @Test
    void shouldReturnReservationsForUser() {
        UserId userId = new UserId(1L);
        Reservation reservation = Reservation.create(
                new ReservationId("01HPQRLSTSRVCTST01ABCDEFA"),
                userId,
                new AvailableSlotId(90L),
                Instant.now().minusSeconds(90)
        );

        when(reservationRepository.findByUserId(userId)).thenReturn(List.of(reservation));

        ReservationListUseCase.Query query = ReservationListUseCase.Query.builder()
                .userId(userId)
                .build();

        List<Reservation> reservations = reservationListService.list(query);

        assertEquals(1, reservations.size());
        assertEquals(userId, reservations.get(0).getUserId());
    }

    @Test
    void shouldThrowWhenUserIdIsMissing() {
        ReservationListUseCase.Query query = ReservationListUseCase.Query.builder().build();

        assertThrows(NullPointerException.class, () -> reservationListService.list(query));
    }
}
