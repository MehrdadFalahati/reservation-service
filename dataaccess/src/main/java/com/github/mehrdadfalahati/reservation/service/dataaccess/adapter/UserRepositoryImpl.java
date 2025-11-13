package com.github.mehrdadfalahati.reservation.service.dataaccess.adapter;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.UserRepository;
import com.github.mehrdadfalahati.reservation.service.dataaccess.mapper.UserDataMapper;
import com.github.mehrdadfalahati.reservation.service.dataaccess.repository.UserJpaRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserDataMapper userDataMapper;

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value())
                .map(userDataMapper::toDomain);
    }
}
