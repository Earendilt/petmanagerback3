package com.petmanager.auth_service;
import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.repository.SystemUserRepository;
import com.petmanager.auth_service.service.UserService;
import com.petmanager.auth_service.web.dto.UserRequest;
import com.petmanager.auth_service.web.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void createUserShouldReturnUserResponse() {
        // Mocks
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        // Configuración del mock
        when(userRepository.existsByUsername("juan")).thenReturn(false);
        when(userRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");

        SystemUser fakeUser = SystemUser.builder()
                .id(1)
                .username("juan")
                .email("juan@test.com")
                .passwordHash("encoded1234")
                .enabled(true)
                .build();

        when(userRepository.save(Mockito.any(SystemUser.class))).thenReturn(fakeUser);

        // Servicio real con mocks
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);

        // Request de prueba
        UserRequest request = UserRequest.builder()
                .username("juan")
                .email("juan@test.com")
                .password("1234")
                .build();

        // Ejecutar
        UserResponse response = userService.createUser(request);

        // Verificar
        assertNotNull(response);
        assertEquals("juan", response.getUsername());
        assertEquals("juan@test.com", response.getEmail());
    }
}