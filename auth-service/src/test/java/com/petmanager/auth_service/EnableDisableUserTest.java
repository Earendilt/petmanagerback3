package com.petmanager.auth_service.service;

import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.repository.SystemUserRepository;
import com.petmanager.auth_service.service.UserService;
import com.petmanager.auth_service.web.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnableDisableUserTest {

    @Test
    void enableUserShouldSetEnabledTrue() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser u = SystemUser.builder().id(5).username("x").enabled(false).build();
        when(userRepository.findById(5)).thenReturn(Optional.of(u));
        when(userRepository.save(any(SystemUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserResponse res = svc.enableUser(5);

        assertTrue(res.getEnabled());
    }

    @Test
    void disableUserShouldSetEnabledFalse() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser u = SystemUser.builder().id(6).username("y").enabled(true).build();
        when(userRepository.findById(6)).thenReturn(Optional.of(u));
        when(userRepository.save(any(SystemUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserResponse res = svc.disableUser(6);

        assertFalse(res.getEnabled());
    }
}