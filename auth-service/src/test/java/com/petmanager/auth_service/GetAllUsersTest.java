package com.petmanager.auth_service.service;

import com.petmanager.auth_service.entity.SystemUser;
import com.petmanager.auth_service.repository.RoleRepository;
import com.petmanager.auth_service.repository.SystemUserRepository;
import com.petmanager.auth_service.service.UserService;
import com.petmanager.auth_service.web.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetAllUsersTest {

    @Test
    void getAllUsersShouldReturnListOfResponses() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        SystemUser u1 = SystemUser.builder().id(1).username("juan").email("x@y.com").enabled(true).build();
        SystemUser u2 = SystemUser.builder().id(2).username("ana").email("a@y.com").enabled(false).build();

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        List<UserResponse> responses = svc.getAllUsers();

        assertEquals(2, responses.size());
        assertEquals("juan", responses.get(0).getUsername());
        assertEquals("ana", responses.get(1).getUsername());
    }

    @Test
    void getAllUsersShouldReturnEmptyListWhenNoUsers() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.findAll()).thenReturn(List.of());

        UserService svc = new UserService(userRepository, roleRepository, passwordEncoder);
        List<UserResponse> responses = svc.getAllUsers();

        assertTrue(responses.isEmpty());
    }
}