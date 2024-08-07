package com.ljakovic.simpleproducts.security.config;

import com.ljakovic.simpleproducts.rest.RequestAndResponseLoggingFilter;
import com.ljakovic.simpleproducts.security.filter.ApiKeyAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/api-docs/**",
            "/swagger-ui/**"
    };

    private final ApiKeyAuthFilter authFilter;
    private final RequestAndResponseLoggingFilter reqResFilter;

    public SecurityConfiguration(ApiKeyAuthFilter authFilter, RequestAndResponseLoggingFilter reqResFilter) {
        this.authFilter = authFilter;
        this.reqResFilter = reqResFilter;
    }

    /**
     * Configures the security filter chain for the application
     * Disables CSRF, CORS, and form login
     * Configures authorization for specific URL patterns
     * and sets the session management policy to stateless
     * Adds custom filters to the security chain
     *
     * @param http The HttpSecurity to configure
     * @return The configured SecurityFilterChain
     * @throws Exception If an error occurs while building the security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(reqResFilter, ApiKeyAuthFilter.class);

        return http.build();
    }
}
