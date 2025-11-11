package com.petmanager.auth_service.service;

import com.petmanager.auth_service.exception.ResourceNotFoundException;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.repository.SystemUserRepository;
import com.petmanager.auth_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteUserTest {

    @Test
    void deleteUserShouldCallRepositoryDelete() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.existsById(7)).thenReturn(true);

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        svc.deleteUser(7);

        verify(userRepository).deleteById(7);
    }

    @Test
    void deleteUserShouldThrowWhenNotExists() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.existsById(8)).thenReturn(false);

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);

        assertThrows(ResourceNotFoundException.class, () -> svc.deleteUser(8));
    }
}