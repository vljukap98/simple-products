package com.ljakovic.simpleproducts.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String apiKey;

    /**
     * Filters incoming HTTP requests to check for a valid API key
     * If the API key is valid, sets the security context with the authentication token
     *
     * @param req The HttpServletRequest to be processed
     * @param res The HttpServletResponse to be processed
     * @param filterChain The FilterChain to pass the request and response to the next filter
     * @throws ServletException If an exception occurs that interferes with the filter's normal operation
     * @throws IOException If an input or output exception occurs
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest req,
            final HttpServletResponse res,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        final String reqApiKey = req.getHeader("api-key");
        if (apiKey.equals(reqApiKey)) {
            final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    reqApiKey,
                    reqApiKey,
                    Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(req, res);
    }
}
