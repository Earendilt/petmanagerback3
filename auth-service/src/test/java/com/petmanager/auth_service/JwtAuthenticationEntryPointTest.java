package com.petmanager.auth_service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationEntryPointTest {

    @Test
    void commenceShouldSetUnauthorizedResponse() throws Exception {
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Invalid token");

        // Capturar el JSON con PrintWriter
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos, true);
        when(response.getWriter()).thenReturn(writer);

        // Act
        entryPoint.commence(request, response, authException);

        // Assert
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String json = baos.toString();
        assertTrue(json.contains("\"status\":401"));
        assertTrue(json.contains("\"error\":\"Unauthorized\""));
        assertTrue(json.contains("\"message\":\"Invalid token\""));
        assertTrue(json.contains("\"path\":\"/api/test\""));
    }
}