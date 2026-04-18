package com.example.apigateway.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final Map<String, String> users = Map.of(
            "alice", "alice123",
            "bob", "bob123",
            "admin", "admin123"
    );

    public Optional<String> authenticate(String username, String password) {
        String configuredPassword = users.get(username);
        if (configuredPassword != null && configuredPassword.equals(password)) {
            return Optional.of(username);
        }
        return Optional.empty();
    }
}

