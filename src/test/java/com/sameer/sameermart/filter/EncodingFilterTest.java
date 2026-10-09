package com.sameer.sameermart.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EncodingFilterTest {

    private EncodingFilter filter;
    private ServletRequest request;
    private ServletResponse response;
    private FilterChain chain;

    @BeforeEach
    public void setUp() {
        filter = new EncodingFilter();
        request = mock(ServletRequest.class);
        response = mock(ServletResponse.class);
        chain = mock(FilterChain.class);
    }

    @Test
    public void testEnforcesUtf8Encoding() throws Exception {
        filter.doFilter(request, response, chain);

        verify(request).setCharacterEncoding("UTF-8");
        verify(response).setCharacterEncoding("UTF-8");
        verify(chain).doFilter(request, response);
    }
}
