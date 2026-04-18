package com.example.apigateway.service;

import com.example.apigateway.model.ApiRequestLog;
import com.example.apigateway.model.RateLimitCounter;
import com.example.apigateway.repository.ApiRequestLogRepository;
import com.example.apigateway.repository.RateLimitCounterRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ApiLogService {

    private final ApiRequestLogRepository apiRequestLogRepository;
    private final RateLimitCounterRepository rateLimitCounterRepository;

    public ApiLogService(
            ApiRequestLogRepository apiRequestLogRepository,
            RateLimitCounterRepository rateLimitCounterRepository
    ) {
        this.apiRequestLogRepository = apiRequestLogRepository;
        this.rateLimitCounterRepository = rateLimitCounterRepository;
    }

    public void logRequest(
            String userId,
            String ipAddress,
            String endpoint,
            String method,
            int statusCode,
            boolean rateLimitViolation,
            long durationMs
    ) {
        ApiRequestLog log = new ApiRequestLog();
        log.setTimestamp(Instant.now());
        log.setUserId(userId);
        log.setIpAddress(ipAddress);
        log.setEndpoint(endpoint);
        log.setMethod(method);
        log.setStatusCode(statusCode);
        log.setRateLimitViolation(rateLimitViolation);
        log.setDurationMs(durationMs);
        apiRequestLogRepository.save(log);
    }

    public void incrementRateCounter(String key) {
        long minute = Instant.now().getEpochSecond() / 60;
        RateLimitCounter counter = rateLimitCounterRepository
                .findByKeyAndWindowStartEpochMinute(key, minute)
                .orElseGet(() -> {
                    RateLimitCounter fresh = new RateLimitCounter();
                    fresh.setKey(key);
                    fresh.setWindowStartEpochMinute(minute);
                    fresh.setRequestCount(0);
                    return fresh;
                });

        counter.setRequestCount(counter.getRequestCount() + 1);
        counter.setUpdatedAt(Instant.now());
        rateLimitCounterRepository.save(counter);
    }

    public List<ApiRequestLog> getRecentLogs(int limit) {
        int safeLimit = Math.max(1, Math.min(200, limit));
        return apiRequestLogRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, safeLimit));
    }
}

