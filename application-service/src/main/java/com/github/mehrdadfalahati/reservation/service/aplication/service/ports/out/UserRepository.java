package com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out;

import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(UserId id);
}
