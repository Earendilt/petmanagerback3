package com.petmanager.auth_service.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void builderShouldCreateErrorResponse() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse response = ErrorResponse.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("User not found")
                .build();

        assertEquals(now, response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("User not found", response.getMessage());
    }

    @Test
    void equalsAndToStringShouldWork() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponse r1 = new ErrorResponse(now, 400, "Bad Request", "Invalid input");
        ErrorResponse r2 = new ErrorResponse(now, 400, "Bad Request", "Invalid input");

        assertEquals(r1, r2); // cubre equals/hashCode
        assertTrue(r1.toString().contains("Bad Request")); // cubre toString
    }

    @Test
    void noArgsConstructorShouldWork() {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(500);
        response.setError("Internal Server Error");
        response.setMessage("Unexpected error");

        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getError());
        assertEquals("Unexpected error", response.getMessage());
    }
}