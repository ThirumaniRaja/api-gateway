package com.example.apigateway.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "rate_limit_counters")
@CompoundIndex(name = "user_window_idx", def = "{'key':1,'windowStartEpochMinute':1}", unique = true)
public class RateLimitCounter {

    @Id
    private String id;

    private String key;

    private long windowStartEpochMinute;

    private long requestCount;

    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

