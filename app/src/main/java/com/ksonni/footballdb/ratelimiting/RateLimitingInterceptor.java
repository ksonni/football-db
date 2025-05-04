package com.ksonni.footballdb.ratelimiting;

import com.ksonni.footballdb.config.RoutesConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final RateLimitingService rateLimitingService;

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
                             final Object handler) throws IOException {
        if (request.getRequestURI().equals(RoutesConfig.GraphQL.PATH)) {
            return true; // Handled by QLRateLimitingInterceptor
        }
        final RateLimitingResult result = rateLimitingService.evaluateRequest(request);
        if (!result.isAcceptable()) {
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), result.getRejectionReason());
            return false;
        }
        return true;
    }

}
