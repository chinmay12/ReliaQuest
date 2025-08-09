package com.reliaquest.api.employee.service.external;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
public class EmployeeRestClientService {

    private final RestTemplate restTemplate;
    private final Retry retryDefault;
    private final String baseUrl;

    Logger logger = LoggerFactory.getLogger(EmployeeRestClientService.class);

    public EmployeeRestClientService(@Value("${rest.client.base-url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = new RestTemplate();

        this.restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = (HttpStatus) response.getStatusCode();
                return statusCode.is5xxServerError() || statusCode == HttpStatus.TOO_MANY_REQUESTS;
            }
        });

        // Retry config for transient failures (timeouts, 5xx)
        RetryConfig defaultRetryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(1), 2.0))
                .retryOnException(this::isRetryableButNotTooManyRequests)
                .build();
        this.retryDefault = Retry.of("defaultRetry", defaultRetryConfig);
    }

    public <T> ResponseEntity<T> callApi(
            String uri, HttpMethod method, Object requestBody, Map<String, String> headers, Class<T> responseType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::set);
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, httpHeaders);

        String fullUrl = baseUrl + uri;

        Supplier<ResponseEntity<T>> apiCall = () -> restTemplate.exchange(fullUrl, method, entity, responseType);

        try {
            return Retry.decorateSupplier(retryDefault, apiCall).get();
        } catch (Exception ex) {
            Throwable cause = ex instanceof InvocationTargetException ? ex.getCause() : ex;
            if (cause instanceof HttpClientErrorException.TooManyRequests) {
                throw (HttpClientErrorException.TooManyRequests) cause;
            }
            logger.error("Unable to get data from Mock server", ex);
            throw handleRetryFailure(ex);
        }
    }

    private boolean isRetryableButNotTooManyRequests(Throwable t) {
        return t instanceof ResourceAccessException
                || t instanceof SocketTimeoutException
                || t instanceof ConnectException
                || t instanceof HttpServerErrorException;
    }

    private RuntimeException handleRetryFailure(Throwable t) {
        Throwable root = (t.getCause() != null) ? t.getCause() : t;
        if (root instanceof HttpStatusCodeException) {
            return (HttpStatusCodeException) root;
        } else {
            return new RuntimeException("API call failed after retries", root);
        }
    }
}
