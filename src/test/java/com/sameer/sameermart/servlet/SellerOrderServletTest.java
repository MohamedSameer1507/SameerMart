package com.sameer.sameermart.servlet;

import com.sameer.sameermart.dao.OrderDAO;
import com.sameer.sameermart.model.SellerOrderItem;
import com.sameer.sameermart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class SellerOrderServletTest {

    private SellerOrderServlet servlet;
    private OrderDAO mockOrderDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new SellerOrderServlet();
        mockOrderDAO = mock(OrderDAO.class);

        Field field = SellerOrderServlet.class.getDeclaredField("orderDAO");
        field.setAccessible(true);
        field.set(servlet, mockOrderDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getContextPath()).thenReturn("/SameerMart");
        when(request.getRequestDispatcher("/seller/orders.jsp")).thenReturn(dispatcher);
    }

    @Test
    public void testDoGetUnauthenticatedRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Please+login+first");
    }

    @Test
    public void testDoGetSessionWithoutUserRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Please+login+first");
    }

    @Test
    public void testDoGetNonSellerRedirectsWithAccessRequired() throws Exception {
        User buyer = new User(1, "Buyer", "buyer@example.com", "secret", "BUYER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(buyer);
        when(session.getAttribute("role")).thenReturn("BUYER");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Seller+access+required");
    }

    @Test
    public void testDoGetSellerAccessSuccessSetsAttributesAndForwards() throws Exception {
        User seller = new User(5, "Seller", "seller@example.com", "secret", "SELLER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(seller);
        when(session.getAttribute("role")).thenReturn("SELLER");

        List<SellerOrderItem> items = new ArrayList<>();
        when(mockOrderDAO.findSellerOrders(5)).thenReturn(items);
        when(mockOrderDAO.getSellerRevenue(5)).thenReturn(new BigDecimal("150.00"));
        when(mockOrderDAO.getSellerTotalOrders(5)).thenReturn(3);

        servlet.doGet(request, response);

        verify(mockOrderDAO).findSellerOrders(5);
        verify(mockOrderDAO).getSellerRevenue(5);
        verify(mockOrderDAO).getSellerTotalOrders(5);

        verify(request).setAttribute("sellerOrders", items);
        verify(request).setAttribute("totalRevenue", new BigDecimal("150.00"));
        verify(request).setAttribute("totalOrders", 3);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPostDelegatesToDoGet() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/SameerMart/login.jsp?error=Please+login+first");
    }
}
