package com.github.mehrdadfalahati.reservation.service.domain.entity;

import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Email;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Password;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Username;

import java.time.Instant;

public class User {
    private UserId userId;
    private Username username;
    private Email email;
    private Password password;
    private Instant createdAt;
}
