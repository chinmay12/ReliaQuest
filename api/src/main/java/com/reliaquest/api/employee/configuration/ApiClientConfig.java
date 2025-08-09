package com.reliaquest.api.employee.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiClientConfig {

    @Bean
    public Bucket rateLimiterBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Bean
    public Retry apiRetry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(1), 2.0))
                .retryOnException(throwable -> throwable
                                instanceof org.springframework.web.client.ResourceAccessException
                        || throwable instanceof java.net.SocketTimeoutException
                        || throwable instanceof java.net.ConnectException
                        || throwable instanceof org.springframework.web.client.HttpServerErrorException
                        || throwable instanceof org.springframework.web.client.HttpClientErrorException.TooManyRequests)
                .build();

        return Retry.of("apiRetry", retryConfig);
    }
}
