package com.github.mehrdadfalahati.reservation.service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCancelUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationCreateUseCase;
import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.in.usecase.ReservationListUseCase;
import com.github.mehrdadfalahati.reservation.service.domain.entity.Reservation;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.*;
import com.github.mehrdadfalahati.reservation.service.security.entity.UserSecurity;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.ReservationCreateRequest;
import com.github.mehrdadfalahati.reservation.service.web.mapper.ReservationApiMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private ReservationCancelUseCase reservationCancelUseCase;

    @MockitoBean
    private ReservationListUseCase reservationListUseCase;

    @Test
    void shouldCreateReservationWithAuthenticatedUser() throws Exception {
        // Given
        Reservation reservation = sampleReservation("01HPQRAPIRES1234567890", ReservationStatus.ACTIVE);
        ReservationCreateRequest request = new ReservationCreateRequest(Instant.parse("2024-12-29T10:00:00Z"));

        when(reservationCreateUseCase.create(any())).thenReturn(reservation);

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .with(user(createMockUserSecurity(1L, "testuser")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("01HPQRAPIRES1234567890"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.availableSlotId").value(5))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldCancelReservationWithAuthenticatedUser() throws Exception {
        // Given
        Reservation cancelledReservation = sampleReservation("01HPQRCANCEL0987654321", ReservationStatus.CANCELLED);
        when(reservationCancelUseCase.cancel(any())).thenReturn(cancelledReservation);

        // When & Then
        mockMvc.perform(delete("/api/reservations/{id}", "01HPQRCANCEL0987654321")
                        .with(user(createMockUserSecurity(1L, "testuser")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldListReservationsForAuthenticatedUser() throws Exception {
        // Given
        Reservation reservation = sampleReservation("01HPQRLIST111111111111", ReservationStatus.ACTIVE);
        when(reservationListUseCase.list(any())).thenReturn(List.of(reservation));

        // When & Then
        mockMvc.perform(get("/api/reservations")
                        .with(user(createMockUserSecurity(1L, "testuser"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("01HPQRLIST111111111111"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoAuthentication() throws Exception {
        // Given
        ReservationCreateRequest request = new ReservationCreateRequest(Instant.parse("2024-12-29T10:00:00Z"));

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenUseCaseThrowsIllegalArgument() throws Exception {
        // Given
        ReservationCreateRequest request = new ReservationCreateRequest(null);
        when(reservationCreateUseCase.create(any()))
                .thenThrow(new IllegalArgumentException("User 99 not found"));

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .with(user(createMockUserSecurity(99L, "testuser99")))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User 99 not found"));
    }

    private UserSecurity createMockUserSecurity(Long userId, String username) {
        return new UserSecurity(User.builder()
                .id(new UserId(userId))
                .username(new Username(username))
                .password(new Password("P@ssw0rd!1"))
                .roles(Set.of(Role.USER))
                .build());
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
