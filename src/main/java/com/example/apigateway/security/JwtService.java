package com.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final String rawSecret;
    private final long expirationSeconds;
    private SecretKey secretKey;

    public JwtService(
            @Value("${gateway.auth.jwt-secret:default-dev-secret-key-change-me-1234567890}") String rawSecret,
            @Value("${gateway.auth.jwt-expiration-seconds:3600}") long expirationSeconds
    ) {
        this.rawSecret = rawSecret;
        this.expirationSeconds = expirationSeconds;
    }

    @PostConstruct
    void init() {
        byte[] keyBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(padKey(keyBytes));
    }

    public String issueToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(secretKey)
                .compact();
    }

    public String validateAndGetSubject(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    private byte[] padKey(byte[] keyBytes) {
        int minLength = 32;
        if (keyBytes.length >= minLength) {
            return keyBytes;
        }

        byte[] padded = new byte[minLength];
        for (int i = 0; i < minLength; i++) {
            padded[i] = keyBytes[i % keyBytes.length];
        }
        return padded;
    }
}

