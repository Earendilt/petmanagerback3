package com.petmanager.auth_service.service;

import com.petmanager.auth_service.entity.RefreshToken;
import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.entity.Role; // si lo usas
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    void builderShouldCreateRefreshToken() {
        Instant now = Instant.now();
        SystemUser user = SystemUser.builder()
                .id(1)
                .username("juan")
                .passwordHash("pwd")
                .enabled(true)
                .role(Role.builder().id(1).name("ADMIN").build())
                .build();

        RefreshToken token = RefreshToken.builder()
                .id(100L)
                .token("abc123")
                .user(user)
                .expiryDate(now)
                .build();

        assertEquals(100L, token.getId());
        assertEquals("abc123", token.getToken());
        assertEquals(user, token.getUser());
        assertEquals(now, token.getExpiryDate());
    }
}