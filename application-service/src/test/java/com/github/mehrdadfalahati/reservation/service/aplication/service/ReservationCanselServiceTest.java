package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCanselUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.AvailableSlotRepository;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationCanselServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private AvailableSlotRepository availableSlotRepository;

    private ReservationCanselService reservationCanselService;

    @BeforeEach
    void setUp() {
        reservationCanselService = new ReservationCanselService(reservationRepository, availableSlotRepository);
    }

    @Test
    void shouldCancelReservationAndReleaseSlot() {
        ReservationId reservationId = new ReservationId("01HPQRSAMPLETESTCANCEL01");
        UserId userId = new UserId(1L);
        AvailableSlotId slotId = new AvailableSlotId(100L);
        Reservation reservation = Reservation.create(reservationId, userId, slotId, Instant.now().minusSeconds(60));
        AvailableSlot slot = AvailableSlot.builder()
                .id(slotId)
                .startTime(Instant.now().plusSeconds(3600))
                .endTime(Instant.now().plusSeconds(7200))
                .isReserved(true)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(availableSlotRepository.findById(slotId)).thenReturn(Optional.of(slot));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(availableSlotRepository.save(any(AvailableSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationCanselUseCase.Command command = ReservationCanselUseCase.Command.builder()
                .reservationId(reservationId)
                .build();

        Reservation result = reservationCanselService.cansel(command);

        assertTrue(result.isCancelled());
        assertFalse(slot.getIsReserved());
    }

    @Test
    void shouldThrowWhenReservationNotFound() {
        ReservationCanselUseCase.Command command = ReservationCanselUseCase.Command.builder()
                .reservationId(new ReservationId("UNKNOWN"))
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationCanselService.cansel(command)
        );

        assertTrue(exception.getMessage().contains("Reservation"));
    }

    @Test
    void shouldThrowWhenSlotNotFound() {
        ReservationId reservationId = new ReservationId("01HPQRSLOTMISSING123456");
        UserId userId = new UserId(5L);
        AvailableSlotId slotId = new AvailableSlotId(77L);
        Reservation reservation = Reservation.create(reservationId, userId, slotId, Instant.now().minusSeconds(120));

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(availableSlotRepository.findById(slotId)).thenReturn(Optional.empty());

        ReservationCanselUseCase.Command command = ReservationCanselUseCase.Command.builder()
                .reservationId(reservationId)
                .build();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> reservationCanselService.cansel(command)
        );

        assertTrue(exception.getMessage().contains("Available slot"));
    }
}
