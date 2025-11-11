package com.petmanager.auth_service.config;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
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
        JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(authException.getMessage()).thenReturn("Invalid token");

        // Capturar el JSON con un ServletOutputStream válido
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ServletOutputStream sos = new ServletOutputStream() {
            @Override
            public void write(int b) {
                baos.write(b);
            }
            @Override
            public boolean isReady() {
                return true;
            }
            @Override
            public void setWriteListener(WriteListener writeListener) {
                // no-op
            }
        };
        when(response.getOutputStream()).thenReturn(sos);

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