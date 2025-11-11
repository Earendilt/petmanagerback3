package com.petmanager.auth_service.service;

import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.exception.ResourceNotFoundException;
import com.petmanager.auth_service.repository.SystemUserRepository;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.service.UserService;
import com.petmanager.auth_service.web.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;   // <--- este import faltaba

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUserTest {

    @Test
    void getUserByIdShouldReturnUserResponse() {
        // Mocks
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        // Usuario simulado
        SystemUser fakeUser = SystemUser.builder()
                .id(1)
                .username("juan")
                .email("juan@test.com")
                .enabled(true)
                .build();

        when(userRepository.findById(1)).thenReturn(Optional.of(fakeUser));

        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);

        // Ejecutar
        UserResponse response = userService.getUserById(1);

        // Verificar
        assertNotNull(response);
        assertEquals("juan", response.getUsername());
        assertEquals("juan@test.com", response.getEmail());
    }

    @Test
    void getUserByIdShouldThrowExceptionWhenNotFound() {
        // Mocks
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);

        // Ejecutar y verificar que lanza excepción
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99));
    }

    @Test
    void getUserByUsernameShouldReturnResponse() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser u = SystemUser.builder().id(2).username("juan").email("x@y.com").enabled(true).build();
        when(userRepository.findByUsername("juan")).thenReturn(Optional.of(u));

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserResponse res = svc.getUserByUsername("juan");

        assertEquals(2, res.getId());
        assertEquals("juan", res.getUsername());
    }

    @Test
    void getAllUsersShouldMapToResponses() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        List<SystemUser> list = List.of(
                SystemUser.builder().id(1).username("a").email("a@x.com").enabled(true).build(),
                SystemUser.builder().id(2).username("b").email("b@x.com").enabled(false).build()
        );
        when(userRepository.findAll()).thenReturn(list);

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        List<UserResponse> res = svc.getAllUsers();

        assertEquals(2, res.size());
        assertEquals("a", res.get(0).getUsername());
        assertFalse(res.get(1).getEnabled());
    }

}