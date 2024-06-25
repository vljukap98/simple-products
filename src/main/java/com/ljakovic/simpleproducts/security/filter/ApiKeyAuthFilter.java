package com.ljakovic.simpleproducts.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
            filterChain.doFilter(req, res);
        } else {
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
