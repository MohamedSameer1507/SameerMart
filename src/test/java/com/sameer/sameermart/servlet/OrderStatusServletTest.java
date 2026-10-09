package com.sameer.sameermart.servlet;

import com.sameer.sameermart.dao.OrderDAO;
import com.sameer.sameermart.model.Order;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OrderStatusServletTest {

    private OrderStatusServlet servlet;
    private OrderDAO mockOrderDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new OrderStatusServlet();
        mockOrderDAO = mock(OrderDAO.class);
        servlet.setOrderDAO(mockOrderDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responseWriter = new StringWriter();

        when(request.getContextPath()).thenReturn("/SameerMart");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("valid-token");
        when(request.getParameter("csrfToken")).thenReturn("valid-token");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testUnauthenticatedFailsWith401() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
        verifyNoInteractions(mockOrderDAO);
    }

    @Test
    public void testBuyerRoleForbiddenWith403() throws Exception {
        User buyer = new User();
        buyer.setId(10);
        buyer.setRole("BUYER");
        when(session.getAttribute("user")).thenReturn(buyer);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Seller or Admin role required");
        verifyNoInteractions(mockOrderDAO);
    }

    @Test
    public void testInvalidCsrfFailsWith403() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getParameter("csrfToken")).thenReturn("wrong-token");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing CSRF token");
        verifyNoInteractions(mockOrderDAO);
    }

    @Test
    public void testMissingParametersFailsWith400() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getParameter("orderId")).thenReturn(null);
        when(request.getParameter("status")).thenReturn("SHIPPED");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID and status are required");
    }

    @Test
    public void testInvalidOrderIdFormatFailsWith400() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getParameter("orderId")).thenReturn("abc");
        when(request.getParameter("status")).thenReturn("SHIPPED");

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
    }

    @Test
    public void testOrderNotFoundFailsWith404() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getParameter("orderId")).thenReturn("999");
        when(request.getParameter("status")).thenReturn("SHIPPED");

        when(mockOrderDAO.findOrderById(999)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
    }

    @Test
    public void testSellerNotAuthorizedForOrderFailsWith403() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getParameter("orderId")).thenReturn("15");
        when(request.getParameter("status")).thenReturn("SHIPPED");

        Order order = new Order(15, 10, new BigDecimal("100.00"), "CONFIRMED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrderById(15)).thenReturn(order);
        when(mockOrderDAO.isSellerForOrder(20, 15)).thenReturn(false);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to update this order");
        verify(mockOrderDAO, never()).updateOrderStatus(anyInt(), anyString());
    }

    @Test
    public void testInvalidStatusTransitionFailsWith409Conflict() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getParameter("orderId")).thenReturn("15");
        when(request.getParameter("status")).thenReturn("PENDING"); // Cannot go from SHIPPED back to PENDING

        Order order = new Order(15, 10, new BigDecimal("100.00"), "SHIPPED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrderById(15)).thenReturn(order);
        when(mockOrderDAO.isSellerForOrder(20, 15)).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).sendError(eq(409), contains("Cannot transition order from SHIPPED to PENDING"));
        verify(mockOrderDAO, never()).updateOrderStatus(anyInt(), anyString());
    }

    @Test
    public void testValidStatusTransitionSellerRedirects() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getParameter("orderId")).thenReturn("15");
        when(request.getParameter("status")).thenReturn("SHIPPED");

        Order order = new Order(15, 10, new BigDecimal("100.00"), "CONFIRMED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrderById(15)).thenReturn(order);
        when(mockOrderDAO.isSellerForOrder(20, 15)).thenReturn(true);
        when(mockOrderDAO.updateOrderStatus(15, "SHIPPED")).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockOrderDAO).updateOrderStatus(15, "SHIPPED");
        verify(response).sendRedirect(contains("/seller/orders?success="));
    }

    @Test
    public void testValidStatusTransitionAdminRedirects() throws Exception {
        User admin = new User();
        admin.setId(1);
        admin.setRole("ADMIN");
        when(session.getAttribute("user")).thenReturn(admin);
        when(request.getParameter("orderId")).thenReturn("15");
        when(request.getParameter("status")).thenReturn("DELIVERED");

        Order order = new Order(15, 10, new BigDecimal("100.00"), "SHIPPED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrderById(15)).thenReturn(order);
        when(mockOrderDAO.updateOrderStatus(15, "DELIVERED")).thenReturn(true);

        servlet.doPost(request, response);

        // Admin does not need isSellerForOrder check
        verify(mockOrderDAO, never()).isSellerForOrder(anyInt(), anyInt());
        verify(mockOrderDAO).updateOrderStatus(15, "DELIVERED");
        verify(response).sendRedirect(contains("/admin/dashboard.jsp?success="));
    }

    @Test
    public void testJsonAcceptHeaderReturnsOkEnvelope() throws Exception {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(request.getParameter("orderId")).thenReturn("15");
        when(request.getParameter("status")).thenReturn("SHIPPED");

        Order order = new Order(15, 10, new BigDecimal("100.00"), "CONFIRMED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrderById(15)).thenReturn(order);
        when(mockOrderDAO.isSellerForOrder(20, 15)).thenReturn(true);
        when(mockOrderDAO.updateOrderStatus(15, "SHIPPED")).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(response).setContentType("application/json;charset=UTF-8");
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("\"status\":\"SHIPPED\""));
    }

    @Test
    public void testIsValidStatusTransitionStateMatrix() {
        // PENDING transitions
        assertTrue(OrderStatusServlet.isValidStatusTransition("PENDING", "CONFIRMED"));
        assertTrue(OrderStatusServlet.isValidStatusTransition("PENDING", "CANCELLED"));
        assertFalse(OrderStatusServlet.isValidStatusTransition("PENDING", "SHIPPED"));
        assertFalse(OrderStatusServlet.isValidStatusTransition("PENDING", "DELIVERED"));

        // CONFIRMED transitions
        assertTrue(OrderStatusServlet.isValidStatusTransition("CONFIRMED", "SHIPPED"));
        assertTrue(OrderStatusServlet.isValidStatusTransition("CONFIRMED", "CANCELLED"));
        assertFalse(OrderStatusServlet.isValidStatusTransition("CONFIRMED", "DELIVERED"));
        assertFalse(OrderStatusServlet.isValidStatusTransition("CONFIRMED", "PENDING"));

        // SHIPPED transitions
        assertTrue(OrderStatusServlet.isValidStatusTransition("SHIPPED", "DELIVERED"));
        assertTrue(OrderStatusServlet.isValidStatusTransition("SHIPPED", "CANCELLED"));
        assertFalse(OrderStatusServlet.isValidStatusTransition("SHIPPED", "CONFIRMED"));

        // Terminal states
        assertFalse(OrderStatusServlet.isValidStatusTransition("DELIVERED", "CONFIRMED"));
        assertFalse(OrderStatusServlet.isValidStatusTransition("DELIVERED", "CANCELLED"));
        assertFalse(OrderStatusServlet.isValidStatusTransition("CANCELLED", "PENDING"));

        // Same status is valid idempotent
        assertTrue(OrderStatusServlet.isValidStatusTransition("CONFIRMED", "CONFIRMED"));
    }
}
