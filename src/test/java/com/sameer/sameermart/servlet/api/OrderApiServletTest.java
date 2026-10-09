package com.sameer.sameermart.servlet.api;

import com.sameer.sameermart.dao.OrderDAO;
import com.sameer.sameermart.model.Order;
import com.sameer.sameermart.model.SellerOrderItem;
import com.sameer.sameermart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OrderApiServletTest {

    private OrderApiServlet servlet;
    private OrderDAO mockOrderDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new OrderApiServlet();
        mockOrderDAO = mock(OrderDAO.class);
        servlet.setOrderDAO(mockOrderDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getSession(false)).thenReturn(session);
    }

    @Test
    public void testGetOrdersUnauthenticatedReturns401() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("UNAUTHORIZED"));
    }

    @Test
    public void testGetOrdersBuyerRoleReturnsBuyerOrders() throws Exception {
        User buyer = new User();
        buyer.setId(10);
        buyer.setRole("BUYER");
        when(session.getAttribute("user")).thenReturn(buyer);

        Order order = new Order(1, 10, new BigDecimal("500.00"), "CONFIRMED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrdersByBuyerId(10)).thenReturn(List.of(order));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("CONFIRMED"));
    }

    @Test
    public void testGetOrdersSellerRoleReturnsSellerOrders() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);

        SellerOrderItem item = new SellerOrderItem(1, new Timestamp(System.currentTimeMillis()), "SHIPPED",
                "Buyer Name", "buyer@test.com", 5, "Product 5", "Cat", "img.jpg", 1, new BigDecimal("100.00"));
        when(mockOrderDAO.findSellerOrders(20)).thenReturn(List.of(item));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("SHIPPED"));
    }

    @Test
    public void testGetOrdersAdminRoleReturnsAllOrders() throws Exception {
        User admin = new User();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(admin);

        Order order = new Order(2, 10, new BigDecimal("999.00"), "DELIVERED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findAllOrders()).thenReturn(List.of(order));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("DELIVERED"));
    }
}
