package com.github.mehrdadfalahati.reservation.service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCanselUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.AvailableSlotId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.ReservationStatus;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.ReservationCreateRequest;
import com.github.mehrdadfalahati.reservation.service.web.mapper.ReservationApiMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReservationController.class)
@Import(ReservationApiMapperImpl.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationCreateUseCase reservationCreateUseCase;

    @MockitoBean
    private ReservationCanselUseCase reservationCanselUseCase;

    @MockitoBean
    private ReservationListUseCase reservationListUseCase;

    @Test
    void shouldCreateReservation() throws Exception {
        Reservation reservation = sampleReservation("01HPQRAPIRES1234567890", ReservationStatus.ACTIVE);
        ReservationCreateRequest request = new ReservationCreateRequest(1L, Instant.parse("2024-12-29T10:00:00Z"));

        when(reservationCreateUseCase.create(any())).thenReturn(reservation);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("01HPQRAPIRES1234567890"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.availableSlotId").value(5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldCancelReservation() throws Exception {
        Reservation cancelledReservation = sampleReservation("01HPQRCANCEL0987654321", ReservationStatus.CANCELLED);
        when(reservationCanselUseCase.cansel(any())).thenReturn(cancelledReservation);

        mockMvc.perform(delete("/api/reservations/{id}", "01HPQRCANCEL0987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldListReservations() throws Exception {
        Reservation reservation = sampleReservation("01HPQRLIST111111111111", ReservationStatus.ACTIVE);
        when(reservationListUseCase.list(any())).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/reservations").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("01HPQRLIST111111111111"));
    }

    @Test
    void shouldReturnBadRequestWhenUseCaseThrowsIllegalArgument() throws Exception {
        ReservationCreateRequest request = new ReservationCreateRequest(99L, null);
        when(reservationCreateUseCase.create(any()))
                .thenThrow(new IllegalArgumentException("User 99 not found"));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User 99 not found"));
    }

    private Reservation sampleReservation(String id, ReservationStatus status) {
        Instant referenceTime = Instant.parse("2024-12-29T09:00:00Z");
        return Reservation.builder()
                .id(new ReservationId(id))
                .userId(new UserId(1L))
                .availableSlotId(new AvailableSlotId(5L))
                .status(status)
                .reservedAt(referenceTime.minusSeconds(3600))
                .cancelledAt(status == ReservationStatus.CANCELLED ? referenceTime : null)
                .createdAt(referenceTime.minusSeconds(3600))
                .updatedAt(referenceTime)
                .version(0L)
                .build();
    }
}
