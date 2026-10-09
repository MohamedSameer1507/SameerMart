package com.sameer.sameermart.filter;

import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Filter that generates a unique UUID for each incoming request, assigns it to
 * SLF4J MDC context as 'requestId', and adds the 'X-Request-Id' response header.
 */
@WebFilter(filterName = "RequestIdFilter", urlPatterns = {"/*"})
public class RequestIdFilter implements Filter {

    public static final String MDC_KEY = "requestId";
    public static final String HEADER_NAME = "X-Request-Id";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestId = null;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            requestId = httpRequest.getHeader(HEADER_NAME);
        }

        if (requestId == null || requestId.trim().isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_KEY, requestId);

        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader(HEADER_NAME, requestId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }

    @Override
    public void destroy() {}
}
