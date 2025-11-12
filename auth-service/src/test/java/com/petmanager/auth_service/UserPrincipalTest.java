package com.petmanager.auth_service.config;

import com.petmanager.auth_service.entity.Role;
import com.petmanager.auth_service.entity.SystemUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    @Test
    void buildShouldReturnUserPrincipal() {
        Role role = Role.builder().id(1).name("ADMIN").build();
        SystemUser user = SystemUser.builder()
                .id(1)
                .username("juan")
                .passwordHash("secret")
                .enabled(true)
                .role(role)
                .build();

        UserPrincipal principal = UserPrincipal.build(user);

        assertEquals("juan", principal.getUsername());
        assertEquals("secret", principal.getPassword());
        assertTrue(principal.isEnabled());
        assertTrue(principal.isAccountNonExpired());
        assertTrue(principal.isAccountNonLocked());
        assertTrue(principal.isCredentialsNonExpired());

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();
        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority());

        assertEquals(user, principal.getUser());
    }

    @Test
    void isEnabledShouldReturnFalseWhenNullOrFalse() {
        Role role = Role.builder().id(2).name("USER").build();
        SystemUser userDisabled = SystemUser.builder()
                .id(2)
                .username("ana")
                .passwordHash("pwd")
                .enabled(false)
                .role(role)
                .build();

        UserPrincipal principalDisabled = UserPrincipal.build(userDisabled);
        assertFalse(principalDisabled.isEnabled());

        SystemUser userNullEnabled = SystemUser.builder()
                .id(3)
                .username("pedro")
                .passwordHash("pwd")
                .enabled(null)
                .role(role)
                .build();

        UserPrincipal principalNull = UserPrincipal.build(userNullEnabled);
        assertFalse(principalNull.isEnabled());
    }
}