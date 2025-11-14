package com.github.mehrdadfalahati.reservation.service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mehrdadfalahati.reservation.service.domain.entity.User;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Password;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Role;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.UserId;
import com.github.mehrdadfalahati.reservation.service.domain.valueobject.Username;
import com.github.mehrdadfalahati.reservation.service.security.entity.UserSecurity;
import com.github.mehrdadfalahati.reservation.service.security.util.JwtUtil;
import com.github.mehrdadfalahati.reservation.service.web.dto.request.LoginRequest;
import com.github.mehrdadfalahati.reservation.service.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void shouldReturnTokenWhenCredentialsValid() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "StrongP@ss1");
        UserSecurity userSecurity = createMockUserSecurity(1L, "testuser");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userSecurity,
                null,
                userSecurity.getAuthorities()
        );

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken(userSecurity, userSecurity.getId())).thenReturn("test-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsInvalid() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    private UserSecurity createMockUserSecurity(Long userId, String username) {
        return new UserSecurity(User.builder()
                .id(new UserId(userId))
                .username(new Username(username))
                .password(new Password("StrongP@ss1"))
                .roles(Set.of(Role.USER))
                .build());
    }
}
