package com.library.management.authentication;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "zPdk2U5PncBo7gGVxzRC+7OA86N4h+CCbu52tiXZsX4=");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 43200000L);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken("admin", "ADMIN");
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    void extractSubject_shouldReturnUsername() {
        String token = jwtService.generateToken("admin", "ADMIN");
        assertThat(jwtService.extractSubject(token)).isEqualTo("admin");
    }

    @Test
    void getStringClaim_shouldReturnRole() {
        String token = jwtService.generateToken("admin", "ADMIN");
        assertThat(jwtService.getStringClaim(token, "role")).isEqualTo("ADMIN");
    }

    @Test
    void isTokenValid_whenValidToken_shouldReturnTrue() {
        String token = jwtService.generateToken("admin", "ADMIN");

        UserDetails userDetails = User.builder()
                .username("admin")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_whenWrongUsername_shouldReturnFalse() {
        String token = jwtService.generateToken("admin", "ADMIN");

        UserDetails userDetails = User.builder()
                .username("librarian")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    void isTokenValid_whenExpiredToken_shouldThrowException() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        String token = jwtService.generateToken("admin", "ADMIN");

        UserDetails userDetails = User.builder()
                .username("admin")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        assertThatThrownBy(() -> jwtService.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }
}