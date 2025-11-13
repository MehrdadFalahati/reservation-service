package com.github.mehrdadfalahati.reservation.service.dataaccess.repository;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
