package com.example.apigateway.rate;

import com.example.apigateway.config.GatewayProperties;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBucketService {

    private final GatewayProperties gatewayProperties;
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketService(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    public boolean allowRequest(String key) {
        TokenBucket bucket = buckets.computeIfAbsent(
                key,
                ignored -> new TokenBucket(
                        gatewayProperties.getBurstCapacity(),
                        gatewayProperties.getRequestsPerMinute()
                )
        );
        return bucket.tryConsume();
    }
}

