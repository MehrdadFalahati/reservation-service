package com.github.mehrdadfalahati.reservation.service.web.controller;

import com.github.mehrdadfalahati.reservation.service.security.entity.UserSecurity;
import com.github.mehrdadfalahati.reservation.service.security.util.JwtUtil;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.LoginRequest;
import com.github.mehrdadfalahati.reservation.service.web.dto.response.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userDetails instanceof UserSecurity userSecurity ? userSecurity.getId() : null;
        if (userId == null) {
            throw new IllegalStateException("Authenticated user must have an identifier");
        }

        String token = jwtUtil.generateToken(userDetails, userId);
        return ResponseEntity.ok(new LoginResponse(token, userId, userDetails.getUsername()));
    }
}
