package com.example.apigateway.rate;

public class TokenBucket {

    private final double capacity;
    private final double refillTokensPerSecond;
    private double availableTokens;
    private long lastRefillNanos;

    public TokenBucket(int capacity, int refillTokensPerMinute) {
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerMinute / 60.0;
        this.availableTokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (availableTokens >= 1.0) {
            availableTokens -= 1.0;
            return true;
        }
        return false;
    }

    private void refill() {
        long nowNanos = System.nanoTime();
        double elapsedSeconds = (nowNanos - lastRefillNanos) / 1_000_000_000.0;
        if (elapsedSeconds <= 0) {
            return;
        }

        availableTokens = Math.min(capacity, availableTokens + elapsedSeconds * refillTokensPerSecond);
        lastRefillNanos = nowNanos;
    }
}

