package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
}
