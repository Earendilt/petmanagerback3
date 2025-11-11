package com.petmanager.auth_service.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRequestTest {

    @Test
    void userRequestShouldStoreValues() {
        UserRequest req = UserRequest.builder()
                .username("juan")
                .email("juan@test.com")
                .password("1234")
                .roleId(1)
                .enabled(true)
                .build();

        assertEquals("juan", req.getUsername());
        assertEquals("juan@test.com", req.getEmail());
        assertEquals("1234", req.getPassword());
        assertEquals(1, req.getRoleId());
        assertTrue(req.getEnabled());
    }
}