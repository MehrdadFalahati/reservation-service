package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = com.github.mehrdadfalahati.reservation.service.dataaccess.config.TestDataAccessConfiguration.class)
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository repository;

    @Test
    void shouldSaveAndRetrieveUser() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        user.setCreatedAt(now);

        // When
        UserEntity saved = repository.save(user);

        // Then
        assertNotNull(saved.getId());
        assertEquals("testuser", saved.getUsername());
        assertEquals("test@example.com", saved.getEmail());
        assertEquals("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy", saved.getPassword());
        assertEquals(now, saved.getCreatedAt());
    }

    @Test
    void shouldFindUserById() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        user.setCreatedAt(now);
        UserEntity saved = repository.save(user);

        // When
        Optional<UserEntity> found = repository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("testuser", found.get().getUsername());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // When
        Optional<UserEntity> found = repository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    void shouldUpdateUser() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        user.setCreatedAt(now);
        UserEntity saved = repository.save(user);

        // When
        saved.setEmail("updated@example.com");
        UserEntity updated = repository.save(saved);

        // Then
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals(saved.getId(), updated.getId());
    }

    @Test
    void shouldSaveMultipleUsers() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        UserEntity user1 = new UserEntity();
        user1.setUsername("multiuser1");
        user1.setEmail("multiuser1@example.com");
        user1.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        user1.setCreatedAt(now);

        UserEntity user2 = new UserEntity();
        user2.setUsername("multiuser2");
        user2.setEmail("multiuser2@example.com");
        user2.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        user2.setCreatedAt(now);

        // When
        repository.save(user1);
        repository.save(user2);
        long count = repository.count();

        // Then
        assertTrue(count >= 2);
    }
}
