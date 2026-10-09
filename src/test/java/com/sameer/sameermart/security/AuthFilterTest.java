package com.sameer.sameermart.security;

import com.sameer.sameermart.filter.AuthFilter;
import com.sameer.sameermart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class AuthFilterTest {

    private AuthFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain chain;
    private HttpSession session;

    @BeforeEach
    public void setUp() {
        filter = new AuthFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        chain = mock(FilterChain.class);
        session = mock(HttpSession.class);

        when(request.getContextPath()).thenReturn("/SameerMart");
    }

    @Test
    public void testUnauthenticatedRequestRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Please+login+first");
        verifyNoInteractions(chain);
    }

    @Test
    public void testIncompleteSessionRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User());
        when(session.getAttribute("userId")).thenReturn(null); // Missing userId
        when(session.getAttribute("role")).thenReturn("BUYER");

        filter.doFilter(request, response, chain);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Please+login+first");
        verifyNoInteractions(chain);
    }

    @Test
    public void testBuyerAccessingSellerRouteIsDenied403() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User(1, "Buyer", "b@test.com", "hash", "BUYER"));
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("BUYER");

        when(request.getRequestURI()).thenReturn("/SameerMart/seller/dashboard.jsp");

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Seller role required.");
        verifyNoInteractions(chain);
    }

    @Test
    public void testSellerAccessingAdminRouteIsDenied403() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User(2, "Seller", "s@test.com", "hash", "SELLER"));
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("SELLER");

        when(request.getRequestURI()).thenReturn("/SameerMart/admin/dashboard.jsp");

        filter.doFilter(request, response, chain);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin role required.");
        verifyNoInteractions(chain);
    }

    @Test
    public void testBuyerAccessingBuyerRouteIsAllowed() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User(1, "Buyer", "b@test.com", "hash", "BUYER"));
        when(session.getAttribute("userId")).thenReturn(1);
        when(session.getAttribute("role")).thenReturn("BUYER");

        when(request.getRequestURI()).thenReturn("/SameerMart/buyer/marketplace");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    public void testSellerAccessingSellerRouteIsAllowed() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User(2, "Seller", "s@test.com", "hash", "SELLER"));
        when(session.getAttribute("userId")).thenReturn(2);
        when(session.getAttribute("role")).thenReturn("SELLER");

        when(request.getRequestURI()).thenReturn("/SameerMart/seller/dashboard.jsp");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testAdminAccessingAdminRouteIsAllowed() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User(3, "Admin", "admin@test.com", "hash", "ADMIN"));
        when(session.getAttribute("userId")).thenReturn(3);
        when(session.getAttribute("role")).thenReturn("ADMIN");

        when(request.getRequestURI()).thenReturn("/SameerMart/admin/dashboard.jsp");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}
