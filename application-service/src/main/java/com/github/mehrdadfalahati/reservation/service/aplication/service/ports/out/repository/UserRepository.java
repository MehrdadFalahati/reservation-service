package com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository;

import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Username;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(UserId id);
    Optional<User> findByUsername(Username username);
}
