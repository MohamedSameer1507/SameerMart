package com.sameer.sameermart.servlet.api;

import com.sameer.sameermart.dao.CartDAO;
import com.sameer.sameermart.model.CartItem;
import com.sameer.sameermart.model.Product;
import com.sameer.sameermart.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

public class CartApiServletTest {

    private CartApiServlet servlet;
    private CartDAO mockCartDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private StringWriter responseWriter;

    @Before
    public void setUp() throws Exception {
        servlet = new CartApiServlet();
        mockCartDAO = mock(CartDAO.class);
        servlet.setCartDAO(mockCartDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(request.getSession(false)).thenReturn(session);
    }

    @Test
    public void testGetCartUnauthenticatedReturns401() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String json = responseWriter.toString();
        Assert.assertTrue(json.contains("\"success\":false"));
        Assert.assertTrue(json.contains("UNAUTHORIZED"));
    }

    @Test
    public void testGetCartForbiddenForSellerReturns403() throws Exception {
        User seller = new User();
        seller.setId(2);
        seller.setRole("SELLER");
        when(session.getAttribute("user")).thenReturn(seller);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String json = responseWriter.toString();
        Assert.assertTrue(json.contains("\"success\":false"));
        Assert.assertTrue(json.contains("FORBIDDEN"));
    }

    @Test
    public void testGetCartSuccessForBuyerReturns200WithTotals() throws Exception {
        User buyer = new User();
        buyer.setId(5);
        buyer.setRole("BUYER");
        when(session.getAttribute("user")).thenReturn(buyer);

        Product p = new Product(1, "Honey", "desc", "Groceries", new BigDecimal("150.00"), 10);
        CartItem item = new CartItem(5, 1, 2, p);
        when(mockCartDAO.getCartItems(5)).thenReturn(List.of(item));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        Assert.assertTrue(json.contains("\"success\":true"));
        Assert.assertTrue(json.contains("\"totalAmount\":300.00"));
        Assert.assertTrue(json.contains("\"totalQuantity\":2"));
    }
}
