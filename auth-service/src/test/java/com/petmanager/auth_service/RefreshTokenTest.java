package com.petmanager.auth_service.service;

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

    @Test
    void equalsAndToStringShouldWork() {
        Instant now = Instant.now();
        SystemUser user = SystemUser.builder().id(2).username("ana").passwordHash("pwd").enabled(true).build();

        RefreshToken t1 = new RefreshToken(1L, "xyz", user, now);
        RefreshToken t2 = new RefreshToken(1L, "xyz", user, now);

        assertEquals(t1, t2); // cubre equals/hashCode
        assertTrue(t1.toString().contains("xyz")); // cubre toString
    }

    @Test
    void noArgsConstructorShouldWork() {
        RefreshToken token = new RefreshToken();
        token.setId(200L);
        token.setToken("zzz");
        token.setExpiryDate(Instant.now());

        assertEquals(200L, token.getId());
        assertEquals("zzz", token.getToken());
        assertNotNull(token.getExpiryDate());
    }
}