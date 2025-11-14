# Reservation Service

End-to-end sample reservation platform showcasing a modular, hexagonal-inspired Spring Boot architecture. Users reserve the nearest available slot while the system enforces concurrency guarantees, JWT-based security, and database migrations through Liquibase.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Module Structure](#module-structure)
- [Concurrency Control](#concurrency-control)
- [Testing Strategy](#testing-strategy)
- [Packaging and Deployment](#packaging-and-deployment)
- [Getting Started](#getting-started)
- [Authentication & Authorization](#authentication--authorization)
- [Swagger / OpenAPI](#swagger--openapi)
- [Reservation Semantics](#reservation-semantics)
- [Load Testing](#load-testing-gatling)

## Architecture Overview

This project implements **Hexagonal Architecture** (Ports & Adapters) combined with **Domain-Driven Design (DDD)** principles, ensuring clean separation of concerns and testability.

### Architectural Pattern

```
container (bootstrap)
    ↓
web (REST API) ←→ security (JWT/auth)
    ↓
application-service (use cases, ports)
    ↓
dataaccess (adapters) ←→ domain (pure business logic)
```

**Key Principles:**
- **Dependency Inversion**: Higher-level modules depend on abstractions (ports), not concrete implementations
- **Pure Domain**: Domain layer has zero external framework dependencies (no Spring, no JPA)
- **Ports & Adapters**: Application-service defines outbound ports (repository interfaces), dataaccess implements adapters
- **Unidirectional Flow**: Dependencies flow inward toward the domain core

## Technology Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.5.7 |
| **Security** | Spring Security + JWT (JJWT 0.12.6) |
| **Persistence** | Spring Data JPA + Hibernate |
| **Database** | H2 (in-memory) with PostgreSQL compatibility mode |
| **Migration** | Liquibase (YAML-based changesets) |
| **API Docs** | Springdoc OpenAPI 3.0 + Swagger UI |
| **Mapping** | MapStruct 1.6.3 |
| **Code Generation** | Lombok |
| **Load Testing** | Gatling 3.14.9 (Scala) |
| **Build Tool** | Maven 3.9+ |

## Module Structure

The application is organized into 7 Maven modules:

### 1. **domain**
Pure business logic with zero external dependencies.

**Components:**
- **Entities**: `Reservation` (aggregate root), `User`, `AvailableSlot`
- **Value Objects**: `ReservationId` (ULID-based), `Email`, `Password`, `Username`, `Role`, `Permission`
- **Events**: `ReservationCreatedEvent`, `ReservationCancelledEvent`
- **Enums**: `ReservationStatus` (ACTIVE, CANCELLED, EXPIRED)

**Key Invariants:**
- Reservations cannot be created with future dates beyond available slots
- Only ACTIVE reservations can be cancelled
- ULID-based identifiers ensure global uniqueness

### 2. **application-service**
Orchestrates use cases and defines repository contracts (ports).

**Use Cases:**
- `ReservationCreateService` – Atomically locks nearest slot and creates reservation
- `ReservationListService` – Retrieves user's reservation history
- `ReservationCancelService` – Cancels reservation and frees the slot

**Outbound Ports:**
- `ReservationRepository` – Save, find by ID, find by user
- `AvailableSlotRepository` – Find and lock nearest available slot
- `UserRepository` – Find by ID, find by username

### 3. **dataaccess**
JPA adapters implementing repository ports.

**Components:**
- **Adapters**: `ReservationRepositoryImpl`, `AvailableSlotRepositoryImpl`, `UserRepositoryImpl`
- **JPA Repositories**: Spring Data JPA repositories with custom queries
- **Data Mappers**: MapStruct mappers converting domain ↔ JPA entities
- **Migrations**: Liquibase changelogs (schema + seed data)

**Critical Query:**
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM AvailableSlotEntity s WHERE s.isReserved = false
        AND s.startTime >= :requestedTime ORDER BY s.startTime ASC LIMIT 1")
Optional<AvailableSlotEntity> findFirstAvailableSlotWithLock(Instant requestedTime);
```

### 4. **security**
JWT-based stateless authentication and Spring Security configuration.

**Components:**
- `JwtUtil` – Token generation, validation, claims extraction
- `SecurityFilter` – JWT extraction and Spring Security context population
- `UserSecurityService` – UserDetailsService implementation
- `SecurityConfig` – Stateless session, filter chain configuration
- `LegacyCompatiblePasswordEncoder` – Supports BCrypt + legacy plaintext migration

**Security Model:**
- Stateless (no server-side sessions)
- JWT tokens with 24-hour expiration
- Bearer token authentication
- Public endpoints: `/api/auth/**`, `/h2-console/**`, `/swagger-ui/**`, `/v3/api-docs/**`

### 5. **web**
REST API layer with controllers, DTOs, and exception handling.

**Endpoints:**
- `POST /api/auth/login` – Authenticate and receive JWT
- `POST /api/reservations` – Create reservation (requires JWT)
- `GET /api/reservations` – List user's reservations (requires JWT)
- `DELETE /api/reservations/{id}` – Cancel reservation (requires JWT)

**Components:**
- Controllers: `AuthController`, `ReservationController`
- DTOs: Request/Response objects for API contracts
- Mappers: Convert DTOs ↔ domain commands/queries
- Exception Handlers: `GlobalExceptionHandler` for centralized error responses
- OpenAPI Configuration: Swagger integration with bearer auth scheme

### 6. **container**
Spring Boot application bootstrap and full-stack integration tests.

**Bootstrap:**
```java
@SpringBootApplication(scanBasePackages = "com.github.mehrdadfalahati.reservation.service")
@EntityScan(basePackages = "com.github.mehrdadfalahati.reservation.service.dataaccess")
@EnableJpaRepositories(basePackages = "com.github.mehrdadfalahati.reservation.service.dataaccess")
```

**Integration Tests:**
- Nearest-slot allocation verification
- Full reservation lifecycle (create → list → cancel)
- **Concurrency test**: Validates pessimistic locking with parallel threads
- OpenAPI schema validation

### 7. **load-test**
Gatling performance tests with configurable load scenarios.

**Features:**
- Ramps from 1 to 50 users over 30 seconds (default)
- Validates response time < 100ms SLA
- Tests full authentication + reservation workflow
- Configurable via JVM properties (`loadtest.baseUrl`, `loadtest.rampUsers`, etc.)

## Concurrency Control

### Problem Statement
When multiple users attempt to reserve the same time slot simultaneously, the system must ensure **exactly one succeeds** without data corruption or double-booking.

### Solution: Pessimistic Locking at Database Layer

#### 1. **Pessimistic Write Lock**
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<AvailableSlotEntity> findFirstAvailableSlotWithLock(Instant requestedTime, Boolean isReserved);
```

**How It Works:**
- When a transaction queries for an available slot, the database acquires an **exclusive row lock**
- Other transactions attempting to read the same row **block** until the lock is released
- Lock is held for the entire transaction duration
- Upon commit, the slot is marked as reserved and the lock is released

#### 2. **Atomic Transaction Boundary**
```java
@Transactional
public Reservation create(Command command) {
    // 1. Lock acquisition
    AvailableSlot slot = availableSlotRepository.findAndLockNearestAvailableSlot(requestedTime)
        .orElseThrow(() -> new NoAvailableSlotException());

    // 2. Mark slot as reserved (still within transaction)
    slot.setIsReserved(true);
    availableSlotRepository.save(slot);

    // 3. Create reservation
    Reservation reservation = Reservation.create(userId, slot.getId(), Instant.now());
    return reservationRepository.save(reservation);

    // 4. Commit transaction → lock released
}
```

#### 3. **Optimistic Locking (Secondary Layer)**
```java
@Version
@Column(name = "version", nullable = false)
private Long version;  // Auto-incremented on each update
```

**Benefits:**
- Provides additional safety at ORM level
- Detects concurrent modifications even if pessimistic lock is bypassed
- Throws `OptimisticLockException` if version mismatch detected

#### 4. **Concurrency Validation Test**
[ReservationSystemIntegrationTest.java:shouldPreventSameSlotBeingBookedConcurrently](container/src/test/java/com/github/mehrdadfalahati/reservation/service/container/ReservationSystemIntegrationTest.java)

```java
ExecutorService executor = Executors.newFixedThreadPool(2);
CountDownLatch startGate = new CountDownLatch(1);

// Two users attempt to reserve simultaneously
Future<ResponseEntity<ReservationResponse>> futureA = executor.submit(() -> {
    startGate.await();  // Wait for signal
    return createReservation(userA, requestedTime);
});

Future<ResponseEntity<ReservationResponse>> futureB = executor.submit(() -> {
    startGate.await();
    return createReservation(userB, requestedTime);
});

startGate.countDown();  // Release both threads at once

// Both succeed but receive DIFFERENT slots
assertThat(responseA.getBody().availableSlotId())
    .isNotEqualTo(responseB.getBody().availableSlotId());
```

#### 5. **Scalability Considerations**

**Connection Pooling (HikariCP):**
```yaml
spring.datasource.hikari:
  maximum-pool-size: 10
  minimum-idle: 5
```

**Stateless Design:**
- No server-side session state (JWT-based auth)
- Enables horizontal scaling without sticky sessions

**Database-Level Concurrency:**
- Delegates contention management to database (PostgreSQL/H2)
- Pessimistic locks ensure serializable isolation for critical sections
- Read operations use optimistic concurrency (version checking)

## Testing Strategy

The project implements a comprehensive testing pyramid with **19 test classes** covering all layers.

### Test Distribution by Layer

| Module | Test Count | Test Types |
|--------|-----------|------------|
| **domain** | 6 tests | Unit tests for entities, value objects, and business rules |
| **application-service** | 3 tests | Use case orchestration tests |
| **dataaccess** | 6 tests | Repository pattern, JPA queries, data mapping |
| **web** | 2 tests | HTTP endpoint contracts, request/response validation |
| **security** | 1 test | JWT generation, validation, authentication flow |
| **container** | 1 test | Full-stack integration tests with concurrency validation |
| **Total** | **19 test classes** | Comprehensive coverage across all layers |

### Test Types

#### 1. **Unit Tests** (Domain Layer)
- **ReservationTest**: Business logic validation (state transitions, invariants)
- **EmailTest, PasswordTest, UsernameTest**: Value object constraint validation
- **RoleTest, PermissionTest**: Security model validation

#### 2. **Service Tests** (Application Layer)
- **ReservationCreateServiceTest**: Use case orchestration, error handling
- **ReservationListServiceTest**: Query validation
- **ReservationCancelServiceTest**: State transition validation

#### 3. **Repository Tests** (Dataaccess Layer)
- **ReservationRepositoryImplTest**: Repository pattern contract validation
- **ReservationJpaRepositoryTest**: JPA query correctness
- **ReservationDataMapperTest**: Domain ↔ Entity mapping accuracy
- **AvailableSlotRepositoryImplTest**: Pessimistic locking verification
- **UserRepositoryImplTest**: User persistence validation

#### 4. **Controller Tests** (Web Layer)
- **ReservationControllerTest**: REST endpoint contracts, HTTP status codes
- **AuthControllerTest**: Authentication flow, JWT issuance

#### 5. **Integration Tests** (Container Module)
[ReservationSystemIntegrationTest.java](container/src/test/java/com/github/mehrdadfalahati/reservation/service/container/ReservationSystemIntegrationTest.java)

Tests the **entire stack** end-to-end using `@SpringBootTest` and `TestRestTemplate`:

```java
@Test
void shouldAllocateNearestSlotForReservationRequests()
// Verifies nearest-slot algorithm works with seeded data

@Test
void shouldSupportFullReservationLifecycleForUser()
// Tests create → list → cancel → re-create workflow

@Test
void shouldPreventSameSlotBeingBookedConcurrently()
// CRITICAL: Validates pessimistic locking with parallel threads
// Uses ExecutorService + CountDownLatch to synchronize concurrent requests

@Test
void shouldExposeOpenApiDocuments()
// Validates OpenAPI schema generation
```

#### 6. **Load Tests** (Gatling)
[ReservationSimulation.scala](load-test/src/test/scala/com/github/mehrdadfalahati/reservation/service/loadtest/ReservationSimulation.scala)

**Performance SLA Validation:**
- **Response Time**: Max 100ms (95th percentile)
- **Success Rate**: ≥ 99% successful requests
- **Concurrent Users**: Ramps from 1 → 50 users over 30 seconds
- **Steady Load**: Holds 100 users for 60 seconds
- **Iterations**: Each user performs 5 reservation cycles

**Scenario Flow:**
```
Login (extract JWT) → Create Reservation → List Reservations → Pause → Repeat
```

**Anti-Stampede Mechanism:**
Time spreading prevents artificial contention:
```scala
val hoursOffset = (userId * 10 + iterationCount * 2) % 1000
val requestedTime = baseTime.plusSeconds(hoursOffset * 3600)
```

### Running Tests

```bash
# Run all tests (excludes load-test by default)
mvn clean test

# Run specific module tests
mvn -pl domain test
mvn -pl application-service test
mvn -pl web test

# Run integration tests
mvn -pl container test

# Run with coverage report
mvn clean verify

# Run load tests (requires running application)
mvn -pl container spring-boot:run  # Terminal 1
mvn -pl load-test gatling:test -Dloadtest.baseUrl=http://localhost:8080  # Terminal 2
```

## Packaging and Deployment

### Build Methods

#### 1. **Standard JAR Packaging**
```bash
# Build executable JAR
mvn clean package -DskipTests

# Run JAR
java -jar container/target/container-1.0-SNAPSHOT.jar
```

#### 2. **Docker Image via Spring Boot Maven Plugin**

The `container` module is configured with Spring Boot's built-in Docker image builder:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <image>
            <name>${project.groupId}/reservation.service:${project.version}</name>
        </image>
    </configuration>
    <executions>
        <execution>
            <phase>install</phase>
            <goals>
                <goal>build-image</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Build Docker Image:**
```bash
# Requires Docker daemon running
mvn clean install -DskipTests

# Image name: com.github.mehrdadfalahati/reservation.service:1.0-SNAPSHOT
docker images | grep reservation.service
```

**Run Docker Container:**
```bash
docker run -p 8080:8080 \
  com.github.mehrdadfalahati/reservation.service:1.0-SNAPSHOT
```

#### 3. **GitHub Container Registry (GHCR)**

The CI/CD pipeline automatically publishes Docker images to GitHub Container Registry on every push to `main`:

```yaml
# Accessible at:
ghcr.io/mehrdadfalahati/reservation-service:latest
ghcr.io/mehrdadfalahati/reservation-service:1.0-SNAPSHOT
```

**Pull and Run from GHCR:**
```bash
# Login to GHCR (if private)
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# Pull image
docker pull ghcr.io/mehrdadfalahati/reservation-service:latest

# Run container
docker run -p 8080:8080 ghcr.io/mehrdadfalahati/reservation-service:latest
```

### Deployment Environments

**Development:**
```bash
mvn -pl container spring-boot:run
```

**Staging/Production (Docker):**
```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e JWT_SECRET=your-secure-secret-key \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/reservation_db \
  -e SPRING_DATASOURCE_USERNAME=dbuser \
  -e SPRING_DATASOURCE_PASSWORD=dbpass \
  ghcr.io/mehrdadfalahati/reservation-service:latest
```

**Environment Variables:**
- `JWT_SECRET` – 256-bit HMAC-SHA256 secret key (required in production)
- `JWT_EXPIRATION` – Token expiration in milliseconds (default: 86400000 = 24h)
- `SPRING_DATASOURCE_URL` – Database connection string (defaults to H2)
- `SPRING_DATASOURCE_USERNAME` / `PASSWORD` – Database credentials

### Database Migration

Liquibase automatically applies migrations on startup:

```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

**Manual Migration:**
```bash
# Validate migrations
mvn liquibase:validate

# Apply migrations manually
mvn liquibase:update

# Rollback last changeset
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker (optional, for containerized deployment)

### Build & Test

```bash
# Clean build everything (excludes load-test in tests)
mvn clean install

# Run focused tests (example)
mvn -pl web test -Dtest=ReservationControllerTest,AuthControllerTest

# Run full-stack integration tests (boots the container module with H2 + Liquibase)
mvn -pl container -am test
```

### Running the Application

```bash
# Starts the aggregated Spring Boot app (listens on port 8080 by default)
mvn -pl container spring-boot:run
```

The runtime uses an in-memory H2 database seeded via Liquibase. Update `container/src/main/resources/application.yml` to point to PostgreSQL or another RDBMS if required.

## Authentication & Authorization

- **Login endpoint:** `POST /api/auth/login`
- **Request body:**

```json
{
  "username": "user1",
  "password": "hashed_password_123"
}
```

- **Seed users:**
  - `user1 / hashed_password_123`
  - `user2 / hashed_password_456`
  - `user3 / hashed_password_789`

The security module ships with a `LegacyCompatiblePasswordEncoder` that transparently supports both BCrypt hashes and the legacy clear-text values stored in the Liquibase changelog. All other endpoints require including `Authorization: Bearer <JWT>` headers.

## Swagger / OpenAPI

- **UI:** `http://localhost:8080/swagger-ui.html`
- **Specs:** `http://localhost:8080/v3/api-docs`

The OpenAPI definition includes a `bearerAuth` scheme. Click “Authorize” in Swagger UI and paste the JWT returned from `/api/auth/login` to interact with protected endpoints directly from the browser.

## Reservation Semantics

- Automatically assigns the **nearest available slot** relative to the requested time.
- Pessimistic locking ensures **two users cannot reserve the same slot simultaneously**.
- Cancellation frees the slot so another reservation can reuse it.
- Designed to scale for *1M+ records* with response times under 100 ms by delegating contention to the database layer.

## Integration Tests

`ReservationSystemIntegrationTest` in the container module boots the entire stack and verifies:

1. Nearest-slot allocation using seeded data.
2. Full lifecycle (create → list → cancel → re-create).
3. Concurrency protection via parallel booking attempts.

Run them with `mvnw -pl container -am test`.

## Load Testing (Gatling)

The `load-test` module contains a Gatling simulation (`ReservationSimulation`) that exercises the full authentication +
reservation workflow under concurrent load. It validates the `< 100 ms` SLA via built-in assertions on the global max
response time and success ratio.

Run the load test against a running instance (typically `mvnw -pl container spring-boot:run`) with:

```bash
mvnw -pl load-test gatling:test \
  -Dgatling.simulationClass=com.github.mehrdadfalahati.reservation.service.loadtest.ReservationSimulation \
  -Dloadtest.baseUrl=http://localhost:8080 \
  -Dloadtest.rampUsers=50 \
  -Dloadtest.holdUsers=100 \
  -Dloadtest.slaMs=100

# Disable native io_uring on Windows (already defaulted via plugin config)
# but you can pass explicitly if running Gatling manually:
-Dio.netty.transport.enableNativeIoUring=false
```

Tunable JVM properties:

- `loadtest.baseUrl` – target environment (defaults to `http://localhost:8080`)
- `loadtest.rampUsers` / `loadtest.holdUsers` – concurrent user count during ramp and steady phases
- `loadtest.rampSeconds` / `loadtest.holdSeconds` – duration for each phase
- `loadtest.iterations` – how many reservation cycles each virtual user performs
- `loadtest.slaMs` – SLA threshold enforced via Gatling assertions

## Next Steps

**Production Readiness:**
- Replace the in-memory H2 datasource with PostgreSQL for production deployment
- Configure external configuration management (Spring Cloud Config, Kubernetes ConfigMaps)
- Implement distributed tracing (OpenTelemetry, Jaeger)
- Add metrics and monitoring (Prometheus, Grafana)

**Security Enhancements:**
- Expand the legacy password migration strategy by re-encoding passwords on successful login
- Implement refresh token rotation for long-lived sessions
- Add rate limiting to prevent brute-force attacks

**Performance Optimization:**
- Run load tests at scale to validate <100ms SLA with 1M+ records
- Implement database read replicas for horizontal scaling
- Add Redis caching for frequently accessed data (user sessions, available slots)

**Feature Enhancements:**
- Add reservation expiration scheduler to auto-cancel abandoned reservations
- Implement notification system (email, SMS) for reservation confirmations
- Add pagination and filtering to reservation list endpoints
