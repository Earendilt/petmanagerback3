package com.petmanager.auth_service.service;

import com.petmanager.auth_service.entity.Role;
import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.exception.DuplicateResourceException;
import com.petmanager.auth_service.exception.ResourceNotFoundException;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.repository.SystemUserRepository;
import com.petmanager.auth_service.web.dto.UserResponse;
import com.petmanager.auth_service.web.dto.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UpdateUserTest {

    @Test
    void updateUserShouldUpdateUsernameAndEmail() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser existing = SystemUser.builder()
                .id(10).username("old").email("old@x.com").enabled(true).build();
        when(userRepository.findById(10)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.existsByEmail("new@x.com")).thenReturn(false);
        when(userRepository.save(any(SystemUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserUpdateRequest req = UserUpdateRequest.builder()
                .username("new").email("new@x.com").build();

        UserResponse res = svc.updateUser(10, req);

        assertEquals("new", res.getUsername());
        assertEquals("new@x.com", res.getEmail());
    }

    @Test
    void updateUserShouldThrowWhenUsernameDuplicate() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser existing = SystemUser.builder().id(10).username("old").email("old@x.com").build();
        when(userRepository.findById(10)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserUpdateRequest req = UserUpdateRequest.builder().username("taken").build();

        assertThrows(DuplicateResourceException.class, () -> svc.updateUser(10, req));
    }

    @Test
    void updateUserShouldThrowWhenEmailDuplicate() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser existing = SystemUser.builder().id(10).username("old").email("old@x.com").build();
        when(userRepository.findById(10)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("dup@x.com")).thenReturn(true);

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserUpdateRequest req = UserUpdateRequest.builder().email("dup@x.com").build();

        assertThrows(DuplicateResourceException.class, () -> svc.updateUser(10, req));
    }

    @Test
    void updateUserShouldChangePasswordAndRoleAndEnabled() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser existing = SystemUser.builder().id(10).username("u").email("u@x.com").enabled(true).build();
        when(userRepository.findById(10)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newpass")).thenReturn("ENC");
        Role role = Role.builder().id(3).name("USER").build();
        when(roleRepository.findById(3)).thenReturn(Optional.of(role));
        when(userRepository.save(any(SystemUser.class))).thenAnswer(inv -> inv.getArgument(0));

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserUpdateRequest req = UserUpdateRequest.builder()
                .password("newpass").roleId(3).enabled(false).build();

        UserResponse res = svc.updateUser(10, req);

        assertEquals(3, res.getRoleId());
        assertEquals("USER", res.getRoleName());
        assertFalse(res.getEnabled());
    }

    @Test
    void updateUserShouldThrowWhenUserNotFound() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        UserUpdateRequest req = UserUpdateRequest.builder().username("new").build();

        assertThrows(ResourceNotFoundException.class, () -> svc.updateUser(99, req));
    }
}