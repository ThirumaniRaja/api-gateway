package com.example.apigateway.repository;

import com.example.apigateway.model.RateLimitCounter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RateLimitCounterRepository extends MongoRepository<RateLimitCounter, String> {

    Optional<RateLimitCounter> findByKeyAndWindowStartEpochMinute(String key, long windowStartEpochMinute);
}

