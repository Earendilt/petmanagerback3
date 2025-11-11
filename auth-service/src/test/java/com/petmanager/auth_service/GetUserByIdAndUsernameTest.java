package com.petmanager.auth_service.service;

import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.exception.ResourceNotFoundException;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.repository.SystemUserRepository;
import com.petmanager.auth_service.service.UserService;
import com.petmanager.auth_service.web.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetUserByIdAndUsernameTest {

    @Test
    void getUserByIdShouldReturnResponse() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser u = SystemUser.builder().id(1).username("juan").email("x@y.com").enabled(true).build();
        when(userRepository.findById(1)).thenReturn(Optional.of(u));

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserResponse res = svc.getUserById(1);

        assertEquals(1, res.getId());
        assertEquals("juan", res.getUsername());
    }

    @Test
    void getUserByIdShouldThrowWhenNotFound() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);

        assertThrows(ResourceNotFoundException.class, () -> svc.getUserById(99));
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
    void getUserByUsernameShouldThrowWhenNotFound() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.findByUsername("nope")).thenReturn(Optional.empty());

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);

        assertThrows(ResourceNotFoundException.class, () -> svc.getUserByUsername("nope"));
    }
}