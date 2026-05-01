package com.example.apigateway.filter;

import com.example.apigateway.rate.TokenBucketService;
import com.example.apigateway.security.JwtService;
import com.example.apigateway.service.ApiLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
public class GatewayFilter extends OncePerRequestFilter {

    public static final String USER_ATTRIBUTE = "gateway.user";

    private final JwtService jwtService;
    private final TokenBucketService tokenBucketService;
    private final ApiLogService apiLogService;
    private final ObjectMapper objectMapper;

    public GatewayFilter(
            JwtService jwtService,
            TokenBucketService tokenBucketService,
            ApiLogService apiLogService,
            ObjectMapper objectMapper
    ) {
        this.jwtService = jwtService;
        this.tokenBucketService = tokenBucketService;
        this.apiLogService = apiLogService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") || path.startsWith("/internal/") || path.startsWith("/error")
                || path.startsWith("/v3/api-docs") || path.startsWith("v3/swagger-ui") || path.startsWith("/swagger-ui") || path.equals("/swagger-ui.html")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startNanos = System.nanoTime();
        String ipAddress = extractClientIp(request);
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        String token = extractBearerToken(request.getHeader("Authorization"));
        if (token == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing Bearer token");
            apiLogService.logRequest(null, ipAddress, endpoint, method, response.getStatus(), false, elapsedMs(startNanos));
            return;
        }

        String userId = jwtService.validateAndGetSubject(token);
        if (userId == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            apiLogService.logRequest(null, ipAddress, endpoint, method, response.getStatus(), false, elapsedMs(startNanos));
            return;
        }

        String key = userId + "|" + ipAddress;
        boolean allowed = tokenBucketService.allowRequest(key);
        apiLogService.incrementRateCounter(key);

        if (!allowed) {
            writeError(response, 429, "Rate limit exceeded");
            apiLogService.logRequest(userId, ipAddress, endpoint, method, response.getStatus(), true, elapsedMs(startNanos));
            return;
        }

        request.setAttribute(USER_ATTRIBUTE, userId);
        filterChain.doFilter(request, response);
        apiLogService.logRequest(userId, ipAddress, endpoint, method, response.getStatus(), false, elapsedMs(startNanos));
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private long elapsedMs(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), Map.of("error", message, "status", status));
    }
}
