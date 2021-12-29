package com.ksonni.footballdb.utils;

import com.ksonni.footballdb.ratelimiting.RateLimitingResult;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

public final class MockUtils {

    private MockUtils() {
    }

    /**
     * Makes the rate limiter to accept all requests for testing.
     *
     * @param rateLimitingService a mocked instance of the service
     */
    public static void disableRateLimiting(final RateLimitingService rateLimitingService) {
        BDDMockito.given(rateLimitingService.evaluateRequest(ArgumentMatchers.any()))
                .willReturn(RateLimitingResult.accept());
    }

    /**
     * Mocks a condition where rate limits have been reached.
     *
     * @param rateLimitingService a mocked instance of the service.
     */
    public static void mockRateLimitReached(final RateLimitingService rateLimitingService) {
        BDDMockito.given(rateLimitingService.evaluateRequest(ArgumentMatchers.any()))
                .willReturn(RateLimitingResult.reject("Too many requests"));
    }

}
