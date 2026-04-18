package com.example.apigateway.rate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBucketTest {

    @Test
    void consumesUntilEmptyThenRefills() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(2, 60);

        assertTrue(bucket.tryConsume());
        assertTrue(bucket.tryConsume());
        assertFalse(bucket.tryConsume());

        Thread.sleep(1_100);
        assertTrue(bucket.tryConsume());
    }
}

