package com.petmanager.auth_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    @Test
    void userPrincipalShouldExposeAuthorities() {
        Role role = Role.builder().id(1).name("ADMIN").build();
        SystemUser user = SystemUser.builder()
                .id(1).username("juan").email("x@y.com")
                .passwordHash("enc").enabled(true).role(role).build();

        UserPrincipal principal = UserPrincipal.fromUser(user);

        assertEquals("juan", principal.getUsername());
        assertTrue(principal.isEnabled());
        assertTrue(principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}