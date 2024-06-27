package com.ljakovic.simpleproducts.rest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@ManagedResource
@Component
public class RequestAndResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestAndResponseLoggingFilter.class);

    private static final String PREFIX_HEADERNAME_HEADERVALUE_FORMAT = "%s %s: %s";

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    /**
     * List of HTTP headers whose values should not be logged.
     */
    private static final List<String> SENSITIVE_HEADERS = Arrays.asList(
            "api-key",
            "authorization",
            "proxy-authorization"
    );

    private boolean enabled = true;

    /**
     * Enable logging of HTTP requests and responses.
     */
    @ManagedOperation(description = "Enable logging of HTTP requests and responses")
    public void enable() {
        this.enabled = true;
    }

    /**
     * Disable logging of HTTP requests and responses.
     */
    @ManagedOperation(description = "Disable logging of HTTP requests and responses")
    public void disable() {
        this.enabled = false;
    }

    /**
     * Performs the filtering for HTTP requests and responses
     *
     * @param request The HttpServletRequest to be processed
     * @param response The HttpServletResponse to be processed
     * @param filterChain The FilterChain to pass the request and response to the next filter
     * @throws ServletException If an exception occurs that interferes with the filter's normal operation
     * @throws IOException If an input or output exception occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    /**
     * Wraps the request and response, performs logging before and after the request is processed
     *
     * @param request The wrapped ContentCachingRequestWrapper
     * @param response The wrapped ContentCachingResponseWrapper
     * @param filterChain The FilterChain to pass the request and response to the next filter
     * @throws ServletException If an exception occurs that interferes with the filter's normal operation
     * @throws IOException If an input or output exception occurs
     */
    protected void doFilterWrapped(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        StringBuilder msg = new StringBuilder();

        try {
            beforeRequest(request, msg);
            filterChain.doFilter(request, response);
        }
        finally {
            afterRequest(request, response, msg);
            if(log.isInfoEnabled()) {
                log.info(msg.toString());
            }
            response.copyBodyToResponse();
        }
    }

    /**
     * Logs details of the HTTP request before it is processed
     *
     * @param request The wrapped ContentCachingRequestWrapper
     * @param msg The StringBuilder to append the log messages
     */
    protected void beforeRequest(
            ContentCachingRequestWrapper request,
            StringBuilder msg
    ) {
        if (enabled && log.isInfoEnabled()) {
            msg.append("\n-- REQUEST --\n");
            logRequestHeader(request, request.getRemoteAddr() + "|>", msg);
        }
    }

    /**
     * Logs details of the HTTP request and response after it is processed
     *
     * @param request The wrapped ContentCachingRequestWrapper
     * @param response The wrapped ContentCachingResponseWrapper
     * @param msg The StringBuilder to append the log messages
     */
    protected void afterRequest(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            StringBuilder msg
    ) {
        if (enabled && log.isInfoEnabled()) {
            logRequestBody(request, request.getRemoteAddr() + "|>", msg);
            msg.append("\n-- RESPONSE --\n");
            logResponse(response, request.getRemoteAddr() + "|<", msg);
        }
    }

    /**
     * Logs the headers of the HTTP request
     *
     * @param request The wrapped ContentCachingRequestWrapper
     * @param prefix The prefix for each log line
     * @param msg The StringBuilder to append the log messages
     */
    private static void logRequestHeader(ContentCachingRequestWrapper request, String prefix, StringBuilder msg) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            msg.append(String.format("%s %s %s", prefix, request.getMethod(), request.getRequestURI())).append("\n");
        } else {
            msg.append(String.format("%s %s %s?%s", prefix, request.getMethod(), request.getRequestURI(), queryString)).append("\n");
        }
        Collections.list(request.getHeaderNames())
                .forEach(headerName ->
                        Collections.list(request.getHeaders(headerName))
                                .forEach(headerValue -> {
                                    if(isSensitiveHeader(headerName)) {
                                        msg.append(String.format(PREFIX_HEADERNAME_HEADERVALUE_FORMAT, prefix, headerName, "*******")).append("\n");
                                    }
                                    else {
                                        msg.append(String.format(PREFIX_HEADERNAME_HEADERVALUE_FORMAT, prefix, headerName, headerValue)).append("\n");
                                    }
                                }));
        msg.append(prefix).append("\n");
    }

    /**
     * Logs the body of the HTTP request if it is present
     *
     * @param request The wrapped ContentCachingRequestWrapper
     * @param prefix The prefix for each log line
     * @param msg The StringBuilder to append the log messages
     */
    private static void logRequestBody(ContentCachingRequestWrapper request, String prefix, StringBuilder msg) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, request.getContentType(), request.getCharacterEncoding(), prefix, msg);
        }
    }

    /**
     * Logs the headers and body of the HTTP response
     *
     * @param response The wrapped ContentCachingResponseWrapper
     * @param prefix The prefix for each log line
     * @param msg The StringBuilder to append the log messages
     */
    private static void logResponse(ContentCachingResponseWrapper response, String prefix, StringBuilder msg) {
        int status = response.getStatus();
        msg.append(String.format("%s %s %s", prefix, status, HttpStatus.valueOf(status).getReasonPhrase())).append("\n");
        response.getHeaderNames()
                .forEach(headerName ->
                        response.getHeaders(headerName)
                                .forEach(headerValue ->
                                {
                                    if(isSensitiveHeader(headerName)) {
                                        msg.append(String.format(PREFIX_HEADERNAME_HEADERVALUE_FORMAT, prefix, headerName, "*******")).append("\n");
                                    }
                                    else {
                                        msg.append(String.format(PREFIX_HEADERNAME_HEADERVALUE_FORMAT, prefix, headerName, headerValue)).append("\n");
                                    }
                                }));
        msg.append(prefix).append("\n");
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            logContent(content, response.getContentType(), response.getCharacterEncoding(), prefix, msg);
        }
    }

    /**
     * Logs the content of the HTTP request or response
     *
     * @param content The content as a byte array
     * @param contentType The content type
     * @param contentEncoding The content encoding
     * @param prefix The prefix for each log line
     * @param msg The StringBuilder to append the log messages
     */
    private static void logContent(byte[] content, String contentType, String contentEncoding, String prefix, StringBuilder msg) {
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        if (visible) {
            try {
                String contentString = new String(content, contentEncoding);
                Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> msg.append(prefix).append(" ").append(line).append("\n"));
            } catch (UnsupportedEncodingException e) {
                msg.append(String.format("%s [%d bytes content]", prefix, content.length)).append("\n");
            }
        } else {
            msg.append(String.format("%s [%d bytes content]", prefix, content.length)).append("\n");
        }
    }

    /**
     * Determine if a given header name should have its value logged
     *
     * @param headerName The HTTP header name
     * @return True if the header is sensitive (i.e. its value should <b>not</b> be logged)
     */
    private static boolean isSensitiveHeader(String headerName) {
        return SENSITIVE_HEADERS.contains(headerName.toLowerCase());
    }

    /**
     * Wraps the HttpServletRequest with a ContentCachingRequestWrapper
     *
     * @param request The original HttpServletRequest
     * @return The wrapped ContentCachingRequestWrapper
     */
    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper contentCachingRequestWrapper) {
            return contentCachingRequestWrapper;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    /**
     * Wraps the HttpServletResponse with a ContentCachingResponseWrapper
     *
     * @param response The original HttpServletResponse
     * @return The wrapped ContentCachingResponseWrapper
     */
    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper contentCachingResponseWrapper) {
            return contentCachingResponseWrapper;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}
