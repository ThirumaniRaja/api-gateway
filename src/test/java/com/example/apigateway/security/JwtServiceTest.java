package com.example.apigateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-key-that-is-long-enough-12345", 60);
        jwtService.init();
    }

    @Test
    void createsAndValidatesToken() {
        String token = jwtService.issueToken("alice");
        String subject = jwtService.validateAndGetSubject(token);

        assertEquals("alice", subject);
    }

    @Test
    void rejectsInvalidToken() {
        assertNull(jwtService.validateAndGetSubject("not-a-token"));
    }
}

