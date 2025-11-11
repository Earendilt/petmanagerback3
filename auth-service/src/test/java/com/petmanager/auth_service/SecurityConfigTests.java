package com.petmanager.auth_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Test
    void passwordEncoderShouldReturnBCrypt() {
        SecurityConfig config = new SecurityConfig(mock(org.springframework.security.core.userdetails.UserDetailsService.class),
                mock(JwtAuthenticationEntryPoint.class),
                mock(JwtAuthenticationFilter.class));
        PasswordEncoder encoder = config.passwordEncoder();
        assertNotNull(encoder);
        assertTrue(encoder.encode("1234").length() > 0);
    }

    @Test
    void authenticationProviderShouldUseUserDetailsService() {
        var uds = mock(org.springframework.security.core.userdetails.UserDetailsService.class);
        SecurityConfig config = new SecurityConfig(uds,
                mock(JwtAuthenticationEntryPoint.class),
                mock(JwtAuthenticationFilter.class));
        AuthenticationProvider provider = config.authenticationProvider();
        assertNotNull(provider);
    }

    @Test
    void authenticationManagerShouldReturnManager() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(authConfig.getAuthenticationManager()).thenReturn(manager);

        SecurityConfig config = new SecurityConfig(mock(org.springframework.security.core.userdetails.UserDetailsService.class),
                mock(JwtAuthenticationEntryPoint.class),
                mock(JwtAuthenticationFilter.class));
        assertEquals(manager, config.authenticationManager(authConfig));
    }

    @Test
    void corsConfigurationSourceShouldReturnConfig() {
        SecurityConfig config = new SecurityConfig(mock(org.springframework.security.core.userdetails.UserDetailsService.class),
                mock(JwtAuthenticationEntryPoint.class),
                mock(JwtAuthenticationFilter.class));
        CorsConfigurationSource source = config.corsConfigurationSource();
        assertNotNull(source);
    }
}