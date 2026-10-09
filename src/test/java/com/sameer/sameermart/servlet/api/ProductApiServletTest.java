package com.sameer.sameermart.servlet.api;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ProductApiServletTest {

    private ProductApiServlet servlet;
    private ProductDAO mockProductDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new ProductApiServlet();
        mockProductDAO = mock(ProductDAO.class);
        servlet.setProductDAO(mockProductDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testGetAllActiveProducts() throws Exception {
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("category")).thenReturn(null);
        when(request.getParameter("search")).thenReturn(null);

        Product p = new Product(1, "Honey", "desc", "Groceries", new BigDecimal("10.00"), 5);
        when(mockProductDAO.findAllActive()).thenReturn(List.of(p));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("Honey"));
    }

    @Test
    public void testGetProductsByCategory() throws Exception {
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("category")).thenReturn("Electronics");

        Product p = new Product(2, "Phone", "desc", "Electronics", new BigDecimal("500.00"), 3);
        when(mockProductDAO.findByCategory("Electronics")).thenReturn(List.of(p));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("Phone"));
    }

    @Test
    public void testGetProductsBySearch() throws Exception {
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("category")).thenReturn(null);
        when(request.getParameter("search")).thenReturn("shirt");

        Product p = new Product(3, "Cotton Shirt", "desc", "Fashion", new BigDecimal("30.00"), 10);
        when(mockProductDAO.searchProducts("shirt")).thenReturn(List.of(p));

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("Cotton Shirt"));
    }

    @Test
    public void testGetProductByIdFound() throws Exception {
        when(request.getParameter("id")).thenReturn("5");

        Product p = new Product(5, "Book", "desc", "Books", new BigDecimal("15.00"), 8);
        when(mockProductDAO.findById(5)).thenReturn(p);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":true"));
        assertTrue(json.contains("Book"));
    }

    @Test
    public void testGetProductByIdNotFoundReturns404() throws Exception {
        when(request.getParameter("id")).thenReturn("99");
        when(mockProductDAO.findById(99)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("NOT_FOUND"));
    }

    @Test
    public void testGetProductByIdInvalidFormatReturns400() throws Exception {
        when(request.getParameter("id")).thenReturn("invalid");

        servlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        String json = responseWriter.toString();
        assertTrue(json.contains("\"success\":false"));
        assertTrue(json.contains("INVALID_ID"));
    }
}
