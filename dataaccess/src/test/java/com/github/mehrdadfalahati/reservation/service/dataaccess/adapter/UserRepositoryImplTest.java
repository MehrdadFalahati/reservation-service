package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.UserEntity;
import com.github.mehrdadfalahati.reservation.service.dataaccess.repository.UserJpaRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Email;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Password;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Username;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = com.github.mehrdadfalahati.reservation.service.dataaccess.config.TestDataAccessConfiguration.class)
@ComponentScan(basePackages = "com.github.mehrdadfalahati.reservation.service.dataaccess")
class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void shouldFindUserById() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("$2a$10$hashedPassword");
        userEntity.setCreatedAt(now);
        UserEntity saved = userJpaRepository.save(userEntity);

        // When
        Optional<User> result = userRepository.findById(new UserId(saved.getId()));

        // Then
        assertTrue(result.isPresent());
        User user = result.get();
        assertEquals(new UserId(saved.getId()), user.getId());
        assertEquals(new Username("testuser"), user.getUsername());
        assertEquals(new Email("test@example.com"), user.getEmail());
        assertEquals(new Password("$2a$10$hashedPassword"), user.getPassword());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // When
        Optional<User> result = userRepository.findById(new UserId(999L));

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void shouldMapAllUserFields() {
        // Given
        Instant createdAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("john_doe");
        userEntity.setEmail("john.doe@example.com");
        userEntity.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        userEntity.setCreatedAt(createdAt);
        UserEntity saved = userJpaRepository.save(userEntity);

        // When
        Optional<User> result = userRepository.findById(new UserId(saved.getId()));

        // Then
        assertTrue(result.isPresent());
        User user = result.get();
        assertNotNull(user.getId());
        assertNotNull(user.getUsername());
        assertNotNull(user.getEmail());
        assertNotNull(user.getPassword());
        assertNotNull(user.getCreatedAt());
    }
}
