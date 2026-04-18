package com.example.apigateway.controller;

import com.example.apigateway.service.DummyInternalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/internal")
public class InternalController {

    private final DummyInternalService dummyInternalService;

    public InternalController(DummyInternalService dummyInternalService) {
        this.dummyInternalService = dummyInternalService;
    }

    @GetMapping("/service-a/hello")
    public Map<String, Object> serviceA(@RequestParam(defaultValue = "anonymous") String user) {
        return dummyInternalService.serviceAHello(user);
    }

    @GetMapping("/service-b/hello")
    public Map<String, Object> serviceB(@RequestParam(defaultValue = "anonymous") String user) {
        return dummyInternalService.serviceBData(user);
    }
}

