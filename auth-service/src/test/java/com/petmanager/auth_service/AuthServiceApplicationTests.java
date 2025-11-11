package com.petmanager.auth_service;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
@Disabled("Desactivado para evitar error de conexión en CI")
@SpringBootTest
class AuthServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void loginWithValidCredentialsShouldReturnToken() {
        String token = authService.login("juan", "password");
        assertNotNull(token);
        assertTrue(token.startsWith("ey")); // típico prefijo de JWT
    }
}
