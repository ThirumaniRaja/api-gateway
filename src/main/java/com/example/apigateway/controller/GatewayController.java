package com.example.apigateway.controller;

import com.example.apigateway.filter.GatewayFilter;
import com.example.apigateway.service.DummyInternalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final DummyInternalService dummyInternalService;

    public GatewayController(DummyInternalService dummyInternalService) {
        this.dummyInternalService = dummyInternalService;
    }

    @GetMapping("/{service}/hello")
    public ResponseEntity<?> routeHello(
            @PathVariable String service,
            @RequestAttribute(name = GatewayFilter.USER_ATTRIBUTE) String userId
    ) {
        return switch (service) {
            case "service-a" -> ResponseEntity.ok(dummyInternalService.serviceAHello(userId));
            case "service-b" -> ResponseEntity.ok(dummyInternalService.serviceBData(userId));
            default -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Unknown service: " + service));
        };
    }
}

