package com.sameer.sameermart.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestIdFilterTest {

    private RequestIdFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;

    @BeforeEach
    public void setUp() {
        filter = new RequestIdFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    public void testGeneratesRequestIdWhenNotProvided() throws Exception {
        when(request.getHeader(RequestIdFilter.HEADER_NAME)).thenReturn(null);

        filter.doFilter(request, response, chain);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(response).setHeader(eq(RequestIdFilter.HEADER_NAME), captor.capture());
        assertNotNull(captor.getValue());
        verify(chain).doFilter(request, response);
    }

    @Test
    public void testPreservesExistingRequestIdHeader() throws Exception {
        String existingId = "client-provided-request-id-12345";
        when(request.getHeader(RequestIdFilter.HEADER_NAME)).thenReturn(existingId);

        filter.doFilter(request, response, chain);

        verify(response).setHeader(RequestIdFilter.HEADER_NAME, existingId);
        verify(chain).doFilter(request, response);
    }
}
