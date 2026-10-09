package com.sameer.sameermart.servlet;

import com.sameer.sameermart.model.User;
import com.sameer.sameermart.model.WishlistItem;
import com.sameer.sameermart.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WishlistServletTest {

    private WishlistServlet servlet;
    private WishlistService mockWishlistService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() {
        servlet = new WishlistServlet();
        mockWishlistService = mock(WishlistService.class);
        servlet.setWishlistService(mockWishlistService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getContextPath()).thenReturn("/SameerMart");
        when(session.getAttribute("csrfToken")).thenReturn("valid-csrf");
        when(request.getParameter("csrfToken")).thenReturn("valid-csrf");
    }

    @Test
    public void testGetUnauthenticatedRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);
        verify(response).sendRedirect(contains("/login.jsp"));
    }

    @Test
    public void testGetNonBuyerRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(new User(1, "Seller", "seller@test.com", "pass", "SELLER"));
        when(session.getAttribute("role")).thenReturn("SELLER");

        servlet.doGet(request, response);
        verify(response).sendRedirect(contains("/login.jsp"));
    }

    @Test
    public void testGetSuccessForwardsToJsp() throws Exception {
        User buyer = new User(1, "Buyer", "buyer@test.com", "pass", "BUYER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(buyer);
        when(session.getAttribute("role")).thenReturn("BUYER");

        List<WishlistItem> items = new ArrayList<>();
        when(mockWishlistService.getWishlist(1)).thenReturn(items);
        when(request.getRequestDispatcher("/buyer/wishlist.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);
        verify(request).setAttribute("wishlistItems", items);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testPostInvalidCsrfReturns403() throws Exception {
        User buyer = new User(1, "Buyer", "buyer@test.com", "pass", "BUYER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(buyer);
        when(session.getAttribute("role")).thenReturn("BUYER");
        when(request.getParameter("csrfToken")).thenReturn("invalid-csrf");

        servlet.doPost(request, response);
        verify(response).sendError(eq(HttpServletResponse.SC_FORBIDDEN), contains("CSRF"));
    }

    @Test
    public void testPostAddActionSuccess() throws Exception {
        User buyer = new User(1, "Buyer", "buyer@test.com", "pass", "BUYER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(buyer);
        when(session.getAttribute("role")).thenReturn("BUYER");
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("productId")).thenReturn("42");

        servlet.doPost(request, response);
        verify(mockWishlistService).addToWishlist(1, 42);
        verify(response).sendRedirect(contains("/buyer/wishlist?success=Added+to+wishlist"));
    }

    @Test
    public void testPostRemoveActionSuccess() throws Exception {
        User buyer = new User(1, "Buyer", "buyer@test.com", "pass", "BUYER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(buyer);
        when(session.getAttribute("role")).thenReturn("BUYER");
        when(request.getParameter("action")).thenReturn("remove");
        when(request.getParameter("productId")).thenReturn("42");

        servlet.doPost(request, response);
        verify(mockWishlistService).removeFromWishlist(1, 42);
        verify(response).sendRedirect(contains("/buyer/wishlist?success=Removed+from+wishlist"));
    }

    @Test
    public void testPostMoveToCartActionSuccess() throws Exception {
        User buyer = new User(1, "Buyer", "buyer@test.com", "pass", "BUYER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(buyer);
        when(session.getAttribute("role")).thenReturn("BUYER");
        when(request.getParameter("action")).thenReturn("moveToCart");
        when(request.getParameter("productId")).thenReturn("42");

        servlet.doPost(request, response);
        verify(mockWishlistService).moveToCart(1, 42);
        verify(response).sendRedirect(contains("/buyer/cart?success="));
    }
}
