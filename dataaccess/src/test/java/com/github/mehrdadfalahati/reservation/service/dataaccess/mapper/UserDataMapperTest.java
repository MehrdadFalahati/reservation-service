package com.github.mehrdadfalahati.reservation.service.dataaccess.mapper;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.UserEntity;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Email;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Password;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Username;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserDataMapperTest {

    private final UserDataMapper mapper = Mappers.getMapper(UserDataMapper.class);

    @Test
    void shouldMapUserEntityToDomain() {
        // Given
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername("john_doe");
        entity.setEmail("john@example.com");
        entity.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        entity.setCreatedAt(Instant.now());

        // When
        User user = mapper.toDomain(entity);

        // Then
        assertNotNull(user);
        assertEquals(new UserId(1L), user.getId());
        assertEquals(new Username("john_doe"), user.getUsername());
        assertEquals(new Email("john@example.com"), user.getEmail());
        assertEquals(new Password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"), user.getPassword());
        assertEquals(entity.getCreatedAt(), user.getCreatedAt());
    }

    @Test
    void shouldMapUserDomainToEntity() {
        // Given
        User user = User.builder()
                .id(new UserId(1L))
                .username(new Username("john_doe"))
                .email(new Email("john@example.com"))
                .password(new Password("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"))
                .createdAt(Instant.now())
                .build();

        // When
        UserEntity entity = mapper.toEntity(user);

        // Then
        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertEquals("john_doe", entity.getUsername());
        assertEquals("john@example.com", entity.getEmail());
        assertEquals("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", entity.getPassword());
        assertEquals(user.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    void shouldHandleNullUserEntity() {
        // When
        User user = mapper.toDomain(null);

        // Then
        assertNull(user);
    }

    @Test
    void shouldHandleNullUserDomain() {
        // When
        UserEntity entity = mapper.toEntity(null);

        // Then
        assertNull(entity);
    }

    @Test
    void shouldMapUserIdValueObject() {
        // Given
        Long id = 123L;

        // When
        UserId userId = mapper.map(id);

        // Then
        assertNotNull(userId);
        assertEquals(123L, userId.value());
    }

    @Test
    void shouldMapUserIdToLong() {
        // Given
        UserId userId = new UserId(123L);

        // When
        Long id = mapper.map(userId);

        // Then
        assertNotNull(id);
        assertEquals(123L, id);
    }

    @Test
    void shouldHandleNullUserId() {
        // When
        UserId userId = mapper.map((Long) null);
        Long id = mapper.map((UserId) null);

        // Then
        assertNull(userId);
        assertNull(id);
    }
}
