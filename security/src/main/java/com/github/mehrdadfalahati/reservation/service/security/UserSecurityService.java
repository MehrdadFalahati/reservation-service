package com.github.mehrdadfalahati.reservation.service.security;

import com.github.mehrdadfalahati.reservation.service.aplication.service.ports.out.repository.UserRepository;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Username;
import com.github.mehrdadfalahati.reservation.service.security.entity.UserSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(new Username(username));

        if (user.isPresent())
            return new UserSecurity(user.get());

        throw new UsernameNotFoundException("The user not fund!");
    }
}
