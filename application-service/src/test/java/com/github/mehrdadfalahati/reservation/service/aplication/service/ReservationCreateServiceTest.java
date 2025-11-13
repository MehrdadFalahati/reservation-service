package com.github.mehrdadfalahati.reservation.service.aplication.service;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.AvailableSlotRepository;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.ReservationRepository;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.UserRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.AvailableSlot;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationCreateServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private AvailableSlotRepository availableSlotRepository;
    @Mock
    private UserRepository userRepository;

    private ReservationCreateService reservationCreateService;

    @BeforeEach
    void setUp() {
        reservationCreateService = new ReservationCreateService(
                reservationRepository,
                availableSlotRepository,
                userRepository
        );
    }

    @Test
    void shouldCreateReservationUsingNearestAvailableSlot() {
        UserId userId = new UserId(1L);
        Instant now = Instant.now().minusSeconds(60);
        AvailableSlot slot = AvailableSlot.builder()
                .id(new AvailableSlotId(10L))
                .startTime(now.plusSeconds(3600))
                .endTime(now.plusSeconds(7200))
                .isReserved(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(availableSlotRepository.findAndLockNearestAvailableSlot(now)).thenReturn(Optional.of(slot));
        when(availableSlotRepository.save(any(AvailableSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation toSave = invocation.getArgument(0);
            return Reservation.create(
                    new ReservationId("01HPQRSAMPLETST01NPQ3D7T02"),
                    toSave.getUserId(),
                    toSave.getAvailableSlotId(),
                    toSave.getReservedAt()
            );
        });

        ReservationCreateUseCase.Command command = ReservationCreateUseCase.Command.builder()
                .userId(userId)
                .requestedTime(now)
                .build();

        Reservation result = reservationCreateService.create(command);

        assertEquals(userId, result.getUserId());
        assertEquals(new AvailableSlotId(10L), result.getAvailableSlotId());
        assertNotNull(result.getId());
        assertTrue(slot.getIsReserved());

        ArgumentCaptor<AvailableSlot> slotCaptor = ArgumentCaptor.forClass(AvailableSlot.class);
        verify(availableSlotRepository).findAndLockNearestAvailableSlot(now);
        verify(availableSlotRepository).save(slotCaptor.capture());
        assertTrue(slotCaptor.getValue().getIsReserved());
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        UserId userId = new UserId(10L);

        ReservationCreateUseCase.Command command = ReservationCreateUseCase.Command.builder()
                .userId(userId)
                .requestedTime(Instant.now().minusSeconds(30))
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationCreateService.create(command)
        );

        assertTrue(exception.getMessage().contains("User"));
    }

    @Test
    void shouldThrowWhenNoAvailableSlotFound() {
        UserId userId = new UserId(1L);
        Instant requestedTime = Instant.now().minusSeconds(45);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(availableSlotRepository.findAndLockNearestAvailableSlot(requestedTime)).thenReturn(Optional.empty());

        ReservationCreateUseCase.Command command = ReservationCreateUseCase.Command.builder()
                .userId(userId)
                .requestedTime(requestedTime)
                .build();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> reservationCreateService.create(command)
        );

        assertTrue(exception.getMessage().contains("No available slots"));
    }
}
