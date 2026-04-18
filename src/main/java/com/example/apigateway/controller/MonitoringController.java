package com.example.apigateway.controller;

import com.example.apigateway.model.ApiRequestLog;
import com.example.apigateway.service.ApiLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/monitor")
public class MonitoringController {

    private final ApiLogService apiLogService;

    public MonitoringController(ApiLogService apiLogService) {
        this.apiLogService = apiLogService;
    }

    @GetMapping("/logs")
    public List<ApiRequestLog> recentLogs(@RequestParam(defaultValue = "20") int limit) {
        return apiLogService.getRecentLogs(limit);
    }
}

