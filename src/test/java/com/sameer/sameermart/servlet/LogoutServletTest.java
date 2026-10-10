package com.sameer.sameermart.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class LogoutServletTest {

    private LogoutServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        servlet = new LogoutServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getContextPath()).thenReturn("/SameerMart");
    }

    @Test
    public void testDoGetWithActiveSessionInvalidatesAndRedirects() throws Exception {
        when(request.getSession(false)).thenReturn(session);

        servlet.doGet(request, response);

        verify(session, times(1)).invalidate();
        verify(response).sendRedirect("/SameerMart/login.jsp?success=You+have+been+logged+out");
    }

    @Test
    public void testDoGetWithoutSessionRedirectsGracefully() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(session, never()).invalidate();
        verify(response).sendRedirect("/SameerMart/login.jsp?success=You+have+been+logged+out");
    }

    @Test
    public void testDoPostDelegatesToDoGet() throws Exception {
        when(request.getSession(false)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session, times(1)).invalidate();
        verify(response).sendRedirect("/SameerMart/login.jsp?success=You+have+been+logged+out");
    }
}
