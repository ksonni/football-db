package com.ksonni.footballdb.utils;

import com.ksonni.footballdb.ratelimiting.RateLimitingResult;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public final class TestUtils {

    private TestUtils() {
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

    /**
     * Wraps items into a page.
     *
     * @param items List of items
     * @param <T>   Type items in the list.
     * @return page
     */
    public static <T> Page<T> buildPage(final List<T> items) {
        return new PageImpl<T>(items, PageRequest.of(0, items.size()), items.size());
    }

    /**
     * Wraps items into a page.
     *
     * @param items List of items
     * @param <T>   Type items in the list.
     * @return page
     */
    public static <T> Page<T> buildPage(final T... items) {
        return new PageImpl<T>(List.of(items), PageRequest.of(0, items.length), items.length);
    }

}
