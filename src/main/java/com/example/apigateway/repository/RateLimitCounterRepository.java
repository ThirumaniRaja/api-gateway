package com.example.apigateway.repository;

import com.example.apigateway.model.RateLimitCounter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RateLimitCounterRepository extends JpaRepository<RateLimitCounter, Long> {

    Optional<RateLimitCounter> findByKeyAndWindowStartEpochMinute(String key, long windowStartEpochMinute);
}