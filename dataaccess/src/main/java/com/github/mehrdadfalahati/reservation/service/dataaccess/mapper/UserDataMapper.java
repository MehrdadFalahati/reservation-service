package com.github.mehrdadfalahati.reservation.service.dataaccess.mapper;

import com.github.mehrdadfalahati.reservation.service.dataaccess.entity.UserEntity;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Email;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Password;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Username;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDataMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User user);

    default UserId map(Long value) {
        return value != null ? new UserId(value) : null;
    }

    default Long map(UserId userId) {
        return userId != null ? userId.value() : null;
    }

    default Username mapUsername(String value) {
        return value != null ? new Username(value) : null;
    }

    default String mapUsername(Username username) {
        return username != null ? username.value() : null;
    }

    default Email mapEmail(String value) {
        return value != null ? new Email(value) : null;
    }

    default String mapEmail(Email email) {
        return email != null ? email.address() : null;
    }

    default Password mapPassword(String value) {
        return value != null ? new Password(value) : null;
    }

    default String mapPassword(Password password) {
        return password != null ? password.value() : null;
    }
}
