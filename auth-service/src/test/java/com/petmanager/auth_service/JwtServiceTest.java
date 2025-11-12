package com.petmanager.auth_service.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();
        jwtService.init(); // genera claves RSA si no hay configuración
    }

    @Test
    void generateAndValidateToken() {
        // Arrange
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "juan", "password", Collections.emptyList()
        );

        // Act
        String token = jwtService.generateToken(userDetails);
        String extractedUsername = jwtService.extractUsername(token);
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertNotNull(token);
        assertEquals("juan", extractedUsername);
        assertTrue(isValid);
    }

    @Test
    void tokenShouldBeInvalidIfUsernameMismatch() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "juan", "password", Collections.emptyList()
        );

        UserDetails otherUser = new org.springframework.security.core.userdetails.User(
                "otro", "password", Collections.emptyList()
        );

        String token = jwtService.generateToken(userDetails);
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}