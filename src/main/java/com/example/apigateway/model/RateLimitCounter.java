package com.example.apigateway.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "rate_limit_counters",
        indexes = {
                @Index(name = "user_window_idx", columnList = "key, window_start_epoch_minute", unique = true)
        }
)
public class RateLimitCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "window_start_epoch_minute", nullable = false)
    private long windowStartEpochMinute;

    @Column(name = "request_count")
    private long requestCount;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getWindowStartEpochMinute() {
        return windowStartEpochMinute;
    }

    public void setWindowStartEpochMinute(long windowStartEpochMinute) {
        this.windowStartEpochMinute = windowStartEpochMinute;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}