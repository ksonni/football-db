package com.ksonni.footballdb.ratelimiting;

import jakarta.servlet.http.HttpServletRequest;

public interface RateLimitingService {

    /**
     * Determines whether a request can be accepted using a rate limiting strategy.
     *
     * @param request HTTP request
     * @return Result indicating if a request can be accepted
     */
    RateLimitingResult evaluateRequest(HttpServletRequest request);

}
