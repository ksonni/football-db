package com.ksonni.footballdb.ratelimiting;

import com.ksonni.footballdb.utils.HttpUtils;
import jakarta.servlet.http.HttpServletRequest;

public interface RateLimitingService {

    /**
     * Determines whether a request can be accepted using a rate limiting strategy.
     *
     * @param request HTTP request
     * @return Result indicating if a request can be accepted
     */
    RateLimitingResult evaluateRequest(HttpServletRequest request);

    /**
     * Helper that determines the current request before applying the rate limiting strategy.
     *
     * @return Result indicating if a request can be accepted
     */
    default RateLimitingResult evaluateRequest() {
        return HttpUtils.getCurrentRequest().map(this::evaluateRequest)
            .orElse(RateLimitingResult.reject("Failed to find request context"));
    }

}
