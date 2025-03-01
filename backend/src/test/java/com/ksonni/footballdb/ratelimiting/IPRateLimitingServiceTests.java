package com.ksonni.footballdb.ratelimiting;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

@ExtendWith(MockitoExtension.class)
public class IPRateLimitingServiceTests {

    private static final int REQUEST_LIMIT = 5;
    private static final Duration DURATION = Duration.ofMillis(200);

    @Mock
    private HttpServletRequest request;
    private IPRateLimitingService service;

    @BeforeEach
    void setup() {
        service = new IPRateLimitingService(REQUEST_LIMIT, DURATION);
        BDDMockito.given(request.getRemoteAddr()).willReturn("0.0.0.0");
    }

    @Test
    void evaluateResultRejectsAfterMaxRequestsReached() {
        performMaxRequests();

        final RateLimitingResult result = service.evaluateRequest(request);
        Assertions.assertFalse(result.isAcceptable());
    }

    @Test
    void evaluateResultAcceptsMoreRequestsAfterTheDurationHasElapsed() throws InterruptedException {
        performMaxRequests();

        Thread.sleep(DURATION.toMillis());

        final RateLimitingResult result = service.evaluateRequest(request);
        Assertions.assertTrue(result.isAcceptable());
    }

    @Test
    void clearFilledBucketsOnlyClearsFilledBuckets() throws InterruptedException {
        performMaxRequests();

        final int cleared = service.clearFilledBuckets();
        Assertions.assertEquals(0, cleared);

        Thread.sleep(DURATION.toMillis());

        final int clearedAfterDuration = service.clearFilledBuckets();
        Assertions.assertEquals(1, clearedAfterDuration);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(request);
    }

    // There is an assumption that this can complete within the max requests duration
    private void performMaxRequests() {
        for (int i = 0; i < REQUEST_LIMIT; i++) {
            final RateLimitingResult result = service.evaluateRequest(request);
            Assertions.assertTrue(result.isAcceptable());
        }
    }

}
