package com.ksonni.footballdb.ratelimiting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RateLimitingResult {

    private final boolean acceptable;
    private final String rejectionReason;

    /**
     * Constructs a result indicating the request can be accepted.
     *
     * @return Acceptable result
     */
    public static RateLimitingResult accept() {
        return new RateLimitingResult(true, null);
    }

    /**
     * Constructs a result indicating the request can not be accepted.
     *
     * @param reason Message explaining why the request could not be accepted
     * @return Acceptable result
     */
    public static RateLimitingResult reject(final String reason) {
        return new RateLimitingResult(false, reason);
    }

}
