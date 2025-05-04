package com.ksonni.footballdb.ratelimiting;

import graphql.ErrorType;
import graphql.GraphqlErrorException;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.SimplePerformantInstrumentation;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class QLRateLimitingInterceptor extends SimplePerformantInstrumentation {
    private final RateLimitingService rateLimitingService;

    @Override
    public DataFetcher<?> instrumentDataFetcher(
        final DataFetcher<?> dataFetcher,
        final InstrumentationFieldFetchParameters parameters,
        final InstrumentationState state
    ) {
        if (parameters.isTrivialDataFetcher()) {
            return dataFetcher;
        }
        final RateLimitingResult result = rateLimitingService.evaluateRequest();
        if (!result.isAcceptable()) {
            throw GraphqlErrorException.newErrorException()
                .message(result.getRejectionReason())
                .errorClassification(ErrorType.DataFetchingException)
                .build();
        }
        return dataFetcher;
    }
}
