package com.service.crypto_analyzer;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A filter that implements rate limiting for incoming HTTP requests based on the client's IP address.
 * <p>
 * This filter uses the Bucket4j library to limit requests per minute for each IP address.
 * If the rate limit is exceeded, the response will return a status code of 429 (Too Many Requests).
 */
@Component
public class RateLimitingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    // A map to store rate-limiting buckets by IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Filters incoming requests, applying rate limiting based on the client's IP address.
     * If the rate limit is exceeded, the request is rejected with a 429 status code.
     *
     * @param request  the incoming {@link ServletRequest}
     * @param response the outgoing {@link ServletResponse}
     * @param chain    the {@link FilterChain} to continue processing the request if the rate limit is not exceeded
     * @throws IOException      if an I/O error occurs during request handling
     * @throws ServletException if an error occurs during request filtering
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String ip = request.getRemoteAddr();  // Obtain the client's IP address

        // Log the IP address of the incoming request
        logger.info("Incoming request from IP: {}", ip);

        // Get or create a rate-limiting bucket for the IP
        Bucket bucket = buckets.computeIfAbsent(ip, this::createNewBucket);

        // Check if the request can consume a token from the bucket
        if (bucket.tryConsume(1)) {
            // Proceed with the request if tokens are available
            logger.debug("Request from IP {} allowed. Tokens remaining: {}", ip, bucket.getAvailableTokens());
            chain.doFilter(request, response);
        } else {
            // Log that the request exceeded the rate limit
            logger.warn("Request from IP {} denied. Rate limit exceeded.", ip);
            // Return a 429 status code if the rate limit is exceeded
            ((HttpServletResponse) response).setStatus(429);
            response.getWriter().write("Rate limit exceeded! Please try again later.");
            response.getWriter().flush();
        }
    }

    /**
     * Creates a new {@link Bucket} for rate limiting an IP address.
     * The bucket is configured to allow up to 10 requests per minute.
     *
     * @param ip the IP address for which to create the bucket
     * @return a {@link Bucket} configured with a rate limit of 10 requests per minute
     */
    private Bucket createNewBucket(String ip) {
        logger.info("Creating a new rate-limiting bucket for IP: {}", ip);
        // Limit to 10 requests per minute for each IP
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Initializes the filter. This method can be overridden for any necessary filter-specific initialization.
     *
     * @param filterConfig the {@link FilterConfig} containing the filter's configuration
     * @throws ServletException if an initialization error occurs
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("RateLimitingFilter initialized.");
    }

    /**
     * Destroys the filter. This method can be overridden to perform any cleanup tasks.
     */
    @Override
    public void destroy() {
        logger.info("RateLimitingFilter destroyed.");
    }
}