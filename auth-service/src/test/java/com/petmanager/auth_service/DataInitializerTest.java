package com.petmanager.auth_service.config;

import com.petmanager.auth_service.entity.Role;
import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.repository.SystemUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class DataInitializerTest {

    @Test
    void shouldCreateAdminUserIfNotExists() {
        // Arrange
        SystemUserRepository userRepo = mock(SystemUserRepository.class);
        RoleRepository roleRepo = mock(RoleRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        AdminProperties props = mock(AdminProperties.class);

        when(props.getUsername()).thenReturn("admin");
        when(props.getEmail()).thenReturn("admin@example.com");
        when(props.getPassword()).thenReturn("securepass");
        when(props.getRoleName()).thenReturn("ADMIN");

        when(userRepo.existsByUsername("admin")).thenReturn(false);
        when(roleRepo.findByName("ADMIN")).thenReturn(Optional.of(new Role(1, "ADMIN", "Admin role")));
        when(encoder.encode("securepass")).thenReturn("hashedpass");

        DataInitializer initializer = new DataInitializer(userRepo, roleRepo, encoder, props);

        // Act
        initializer.run();

        // Assert
        verify(userRepo).save(argThat(user ->
                user.getUsername().equals("admin") &&
                        user.getEmail().equals("admin@example.com") &&
                        user.getPasswordHash().equals("hashedpass") &&
                        user.getRole().getName().equals("ADMIN")
        ));
    }
}