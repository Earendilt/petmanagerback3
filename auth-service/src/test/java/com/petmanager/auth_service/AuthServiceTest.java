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


    @Test
    void createUserShouldThrowWhenUsernameExists() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.existsByUsername("juan")).thenReturn(true);

        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        UserRequest req = UserRequest.builder().username("juan").email("x@y.com").password("123").build();

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(req));
    }

    @Test
    void createUserShouldThrowWhenEmailExists() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        when(userRepository.existsByUsername("juan")).thenReturn(false);
        when(userRepository.existsByEmail("x@y.com")).thenReturn(true);

        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        UserRequest req = UserRequest.builder().username("juan").email("x@y.com").password("123").build();

        assertThrows(DuplicateResourceException.class, () -> userService.createUser(req));
    }

    @Test
    void createUserShouldAssignRoleWhenRoleIdProvided() {
        SystemUserRepository userRepository = mock(SystemUserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("enc");
        Role role = Role.builder().id(7).name("ADMIN").build();
        when(roleRepository.findById(7)).thenReturn(Optional.of(role));

        when(userRepository.save(any(SystemUser.class))).thenAnswer(inv -> {
            SystemUser u = inv.getArgument(0);
            u.setId(1);
            return u;
        });

        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);

        UserRequest req = UserRequest.builder()
                .username("juan").email("x@y.com").password("123").roleId(7).enabled(true).build();

        UserResponse res = userService.createUser(req);

        assertEquals(1, res.getId());
        assertEquals("ADMIN", res.getRoleName());
        assertEquals(7, res.getRoleId());
    }

}
