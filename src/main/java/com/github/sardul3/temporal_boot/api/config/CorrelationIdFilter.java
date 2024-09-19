package com.github.sardul3.temporal_boot.api.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        // If the header is missing, generate a new UUID for the correlation ID
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String correlationIdChecked = correlationId;

        // Use a custom wrapper to add the correlation ID to the request headers
        HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if (CORRELATION_ID_HEADER.equals(name)) {
                    return correlationIdChecked;
                }
                return super.getHeader(name);
            }
        };

        // Also set the correlation ID in the response headers
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        // Continue with the filter chain, passing the wrapped request
        filterChain.doFilter(wrappedRequest, response);
    }
}
