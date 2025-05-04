package com.ksonni.footballdb.config;

import com.ksonni.footballdb.ratelimiting.QLRateLimitingInterceptor;
import com.ksonni.footballdb.ratelimiting.RateLimitingService;
import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.scalars.ExtendedScalars;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.List;

@Configuration
public class GraphQLConfig {

    @Value("${app.max-ql-complexity}")
    private Integer maxQlComplexity;

    /**
     * Supports DateTime custom primitive in GraphQL types.
     * @return configurer
     */
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.DateTime);
    }

    /**
     * Returns a GraphQL instrumentation object that's used to enforce complexity & rate limits.
     *
     * @param rateLimitingService shared rate limits service
     * @return Instrumentation
     */
    @Bean
    public Instrumentation queryInstrumentation(final RateLimitingService rateLimitingService) {
        return new ChainedInstrumentation(List.of(
            new MaxQueryComplexityInstrumentation(maxQlComplexity),
            new QLRateLimitingInterceptor(rateLimitingService)
        ));
    }
}
