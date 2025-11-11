package com.petmanager.auth_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationEntryPointTest {

    @Test
    void commenceShouldSetUnauthorizedResponse() throws IOException {
        // Arrange
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Invalid token");

        // Capturar el output JSON
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public void write(int b) {
                baos.write(b);
            }
        };
        when(response.getOutputStream()).thenReturn(sos);

        // Act
        entryPoint.commence(request, response, authException);

        // Assert
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Verificar que el JSON contiene los campos esperados
        String json = baos.toString();
        assertTrue(json.contains("\"status\":401"));
        assertTrue(json.contains("\"error\":\"Unauthorized\""));
        assertTrue(json.contains("\"message\":\"Invalid token\""));
        assertTrue(json.contains("\"path\":\"/api/test\""));
    }
}