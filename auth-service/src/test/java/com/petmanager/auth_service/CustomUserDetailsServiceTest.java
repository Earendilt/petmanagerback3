package com.petmanager.auth_service.service;

import com.petmanager.auth_service.config.UserPrincipal;
import com.petmanager.auth_service.entity.Role;
import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.repository.SystemUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Test
    void loadUserByUsernameShouldReturnUserPrincipal() {
        // Arrange
        SystemUserRepository repository = mock(SystemUserRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        SystemUser user = new SystemUser();
        user.setUsername("juan");
        user.setPasswordHash("hashed");
        user.setEnabled(true);
        user.setRole(new Role(1, "admin", "Administrador del sistema"));

        when(repository.findByUsernameOrEmailWithRole("juan", "juan"))
                .thenReturn(Optional.of(user));

        // Act
        UserPrincipal principal = (UserPrincipal) service.loadUserByUsername("juan");

        // Assert
        assertEquals("juan", principal.getUsername());
        assertEquals("hashed", principal.getPassword());
        assertTrue(principal.isEnabled());
        assertEquals("ROLE_ADMIN", principal.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsernameShouldThrowExceptionIfUserNotFound() {
        // Arrange
        SystemUserRepository repository = mock(SystemUserRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repository);

        when(repository.findByUsernameOrEmailWithRole("nope", "nope"))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("nope")
        );

        assertEquals("User not found with username or email: nope", ex.getMessage());
    }
}