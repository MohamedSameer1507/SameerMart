package com.sameer.sameermart.servlet;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

public class DeleteProductServletTest {

    private DeleteProductServlet servlet;
    private ProductDAO mockProductDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new DeleteProductServlet();
        mockProductDAO = mock(ProductDAO.class);

        Field daoField = DeleteProductServlet.class.getDeclaredField("productDAO");
        daoField.setAccessible(true);
        daoField.set(servlet, mockProductDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getContextPath()).thenReturn("/SameerMart");
        when(session.getAttribute("csrfToken")).thenReturn("valid-csrf-token");
        when(request.getParameter("csrfToken")).thenReturn("valid-csrf-token");
    }

    @Test
    public void testPostUnauthenticatedRedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);
        verify(response).sendRedirect("/SameerMart/login.jsp?error=Please+login+first");
    }

    @Test
    public void testPostNonSellerRedirects() throws Exception {
        User buyer = new User(2, "Buyer", "b@test.com", "pass", "BUYER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(buyer);
        when(session.getAttribute("role")).thenReturn("BUYER");

        servlet.doPost(request, response);
        verify(response).sendRedirect("/SameerMart/login.jsp?error=Seller+access+required");
    }

    @Test
    public void testPostInvalidProductIdRedirects() throws Exception {
        User seller = new User(1, "Seller", "s@test.com", "pass", "SELLER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(seller);
        when(session.getAttribute("role")).thenReturn("SELLER");
        when(request.getParameter("id")).thenReturn("oops");

        servlet.doPost(request, response);
        verify(response).sendRedirect("/SameerMart/seller/dashboard.jsp?error=Invalid+Product+ID");
    }

    @Test
    public void testPostOtherSellersProductRejected() throws Exception {
        User seller = new User(1, "Seller A", "a@test.com", "pass", "SELLER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(seller);
        when(session.getAttribute("role")).thenReturn("SELLER");
        when(request.getParameter("id")).thenReturn("99");
        when(request.getParameter("redirect")).thenReturn("/seller/dashboard.jsp");

        // deleteProduct is scoped to (id, sellerId); product 99 belongs to another seller
        when(mockProductDAO.deleteProduct(99, 1)).thenReturn(ProductDAO.DeletionResult.NOT_FOUND);

        servlet.doPost(request, response);
        verify(response).sendRedirect("/SameerMart/seller/dashboard.jsp?error=Product+not+found+or+unauthorized");
    }

    @Test
    public void testPostOwnProductDeleted() throws Exception {
        User seller = new User(1, "Seller A", "a@test.com", "pass", "SELLER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(seller);
        when(session.getAttribute("role")).thenReturn("SELLER");
        when(request.getParameter("id")).thenReturn("5");
        when(request.getParameter("redirect")).thenReturn("/seller/dashboard.jsp");

        when(mockProductDAO.deleteProduct(5, 1)).thenReturn(ProductDAO.DeletionResult.DELETED);

        servlet.doPost(request, response);
        verify(response).sendRedirect("/SameerMart/seller/dashboard.jsp?success=Product+deleted+successfully");
    }
}