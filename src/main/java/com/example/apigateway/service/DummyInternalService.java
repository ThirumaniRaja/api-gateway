package com.example.apigateway.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class DummyInternalService {

    public Map<String, Object> serviceAHello(String userId) {
        return Map.of(
                "service", "service-a",
                "message", "hello from internal service A",
                "user", userId,
                "timestamp", Instant.now().toString()
        );
    }

    public Map<String, Object> serviceBData(String userId) {
        return Map.of(
                "service", "service-b",
                "data", "sample data payload",
                "user", userId,
                "timestamp", Instant.now().toString()
        );
    }
}

