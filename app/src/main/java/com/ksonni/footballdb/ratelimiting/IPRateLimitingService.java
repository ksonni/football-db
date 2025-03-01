package com.ksonni.footballdb.ratelimiting;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Rate limits requests based on the IP address of the source using the token bucket algorithm.
 */
@Slf4j
public class IPRateLimitingService implements RateLimitingService {

    private final int maxRequests;
    private final Duration duration;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Constructs a service that limits requests based on the IP address of the source.
     *
     * @param maxRequests Max number of requests to allow in the specified duration
     * @param duration    Duration in which to enforce the maxRequests limit
     */
    public IPRateLimitingService(final int maxRequests, final Duration duration) {
        this.maxRequests = maxRequests;
        this.duration = duration;
    }

    @Override
    public RateLimitingResult evaluateRequest(final HttpServletRequest request) {
        final String ip = request.getRemoteAddr();
        if (ip == null || ip.isBlank()) {
            return RateLimitingResult.reject("Unable to determine IP address");
        }

        final Bucket bucket = cache.computeIfAbsent(ip, this::makeBucket);
        final boolean consumed = bucket.tryConsume(1);

        return consumed ? RateLimitingResult.accept()
                : RateLimitingResult.reject(constructLimitExceededMessage());
    }

    /**
     * Clears buckets that have reached max number of tokens, from the cache.
     * This is run periodically by the service to free up memory.
     *
     * @return The number of buckets that were cleared
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public int clearFilledBuckets() {
        final Set<String> keys = cache.keySet();
        int bucketsRemoved = 0;

        log.info("Running cache clean up");
        for (String key : keys) {
            final Bucket bucket = cache.get(key);
            if (bucket.getAvailableTokens() == maxRequests) {
                cache.remove(key);
                bucketsRemoved++;
            }
        }
        log.info("Removed {} buckets from the cache", bucketsRemoved);

        return bucketsRemoved;
    }

    private Bucket makeBucket(final String ipAddress) {
        final var limit = Bandwidth.builder().capacity(maxRequests)
                .refillIntervally(maxRequests, duration).build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String constructLimitExceededMessage() {
        return String.format("Request limit reached. Try again in about %s",
                duration.toString());
    }

}
