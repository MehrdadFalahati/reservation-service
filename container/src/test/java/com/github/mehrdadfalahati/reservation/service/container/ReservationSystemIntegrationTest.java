package com.github.mehrdadfalahati.reservation.service.container;

import com.github.mehrdadfalahati.reservation.service.web.dto.request.LoginRequest;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.ReservationCreateRequest;
import com.github.mehrdadfalahati.reservation.service.web.dto.response.LoginResponse;
import com.github.mehrdadfalahati.reservation.service.web.dto.response.ReservationResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationSystemIntegrationTest {

    private static final Instant SLOT_09 = Instant.parse("2024-12-29T09:00:00Z");
    private static final Instant SLOT_0930 = Instant.parse("2024-12-29T09:30:00Z");
    private static final Instant DAY_BEFORE = Instant.parse("2024-12-28T00:00:00Z");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAllocateNearestSlotForReservationRequests() {
        String token = login("user1", "hashed_password_123");

        ReservationResponse earlySlot = createReservation(token, DAY_BEFORE);
        assertThat(earlySlot.availableSlotId()).isNotNull();
        assertThat(earlySlot.status()).isEqualTo("ACTIVE");

        ReservationResponse nextSlot = createReservation(token, SLOT_0930);
        assertThat(nextSlot.availableSlotId()).isGreaterThan(earlySlot.availableSlotId());
        assertThat(nextSlot.status()).isEqualTo("ACTIVE");
    }

    @Test
    void shouldSupportFullReservationLifecycleForUser() {
        String token = login("user2", "hashed_password_456");

        ReservationResponse reservation = createReservation(token, SLOT_09);
        List<ReservationResponse> listed = listReservations(token);
        assertThat(listed).hasSize(1);
        assertThat(listed.getFirst().status()).isEqualTo("ACTIVE");

        ReservationResponse cancelled = cancelReservation(token, reservation.id());
        assertThat(cancelled.status()).isEqualTo("CANCELLED");

        ReservationResponse recreated = createReservation(token, SLOT_09);
        assertThat(recreated.availableSlotId()).isEqualTo(reservation.availableSlotId());
        assertThat(recreated.id()).isNotEqualTo(reservation.id());
    }

    @Test
    void shouldPreventSameSlotBeingBookedConcurrently() throws Exception {
        String tokenA = login("user1", "hashed_password_123");
        String tokenB = login("user3", "hashed_password_789");

        CountDownLatch startGate = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Callable<ResponseEntity<ReservationResponse>> taskA = () -> performCreate(tokenA, startGate);
            Callable<ResponseEntity<ReservationResponse>> taskB = () -> performCreate(tokenB, startGate);

            Future<ResponseEntity<ReservationResponse>> futureA = executor.submit(taskA);
            Future<ResponseEntity<ReservationResponse>> futureB = executor.submit(taskB);

            startGate.countDown();

            ResponseEntity<ReservationResponse> responseA = futureA.get(5, TimeUnit.SECONDS);
            ResponseEntity<ReservationResponse> responseB = futureB.get(5, TimeUnit.SECONDS);

            assertThat(responseA.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(responseB.getStatusCode()).isEqualTo(HttpStatus.CREATED);

            Long slotA = Objects.requireNonNull(responseA.getBody()).availableSlotId();
            Long slotB = Objects.requireNonNull(responseB.getBody()).availableSlotId();

            assertThat(Set.of(slotA, slotB)).hasSize(2);
        } finally {
            executor.shutdownNow();
        }
    }

    private ResponseEntity<ReservationResponse> performCreate(String token, CountDownLatch startGate) throws InterruptedException {
        startGate.await();
        HttpEntity<ReservationCreateRequest> entity = authorizedEntity(
                new ReservationCreateRequest(SLOT_09),
                token
        );
        return restTemplate.exchange(
                "/api/reservations",
                HttpMethod.POST,
                entity,
                ReservationResponse.class
        );
    }

    private ReservationResponse createReservation(String token, Instant requestedTime) {
        ResponseEntity<ReservationResponse> response = restTemplate.exchange(
                "/api/reservations",
                HttpMethod.POST,
                authorizedEntity(new ReservationCreateRequest(requestedTime), token),
                ReservationResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return Objects.requireNonNull(response.getBody());
    }

    private ReservationResponse cancelReservation(String token, String reservationId) {
        ResponseEntity<ReservationResponse> response = restTemplate.exchange(
                "/api/reservations/{id}",
                HttpMethod.DELETE,
                authorizedEntity(null, token),
                ReservationResponse.class,
                reservationId
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return Objects.requireNonNull(response.getBody());
    }

    private List<ReservationResponse> listReservations(String token) {
        ResponseEntity<List<ReservationResponse>> response = restTemplate.exchange(
                "/api/reservations",
                HttpMethod.GET,
                authorizedEntity(null, token),
                new ParameterizedTypeReference<>() {}
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return Objects.requireNonNull(response.getBody());
    }

    private String login(String username, String password) {
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
                "/api/auth/login",
                new LoginRequest(username, password),
                LoginResponse.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponse body = Objects.requireNonNull(response.getBody());
        assertThat(body.token()).isNotBlank();
        return body.token();
    }

    private <T> HttpEntity<T> authorizedEntity(T body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return new HttpEntity<>(body, headers);
    }
}
