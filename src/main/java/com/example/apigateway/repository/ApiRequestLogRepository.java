package com.example.apigateway.repository;

import com.example.apigateway.model.ApiRequestLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApiRequestLogRepository extends MongoRepository<ApiRequestLog, String> {

    List<ApiRequestLog> findAllByOrderByTimestampDesc(Pageable pageable);
}

