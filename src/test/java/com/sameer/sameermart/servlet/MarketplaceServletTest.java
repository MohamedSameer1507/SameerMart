package com.sameer.sameermart.servlet;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class MarketplaceServletTest {

    private MarketplaceServlet servlet;
    private ProductDAO mockProductDAO;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    @BeforeEach
    public void setUp() throws Exception {
        servlet = new MarketplaceServlet();
        mockProductDAO = mock(ProductDAO.class);

        Field field = MarketplaceServlet.class.getDeclaredField("productDAO");
        field.setAccessible(true);
        field.set(servlet, mockProductDAO);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/buyer/marketplace.jsp")).thenReturn(dispatcher);
    }

    @Test
    public void testDoGetFetchesAvailableProductsAndForwards() throws Exception {
        List<Product> products = new ArrayList<>();
        Product p = new Product(1, "Laptop", "High end", "Electronics", new BigDecimal("999.99"), 10, "laptop.jpg");
        products.add(p);

        when(mockProductDAO.findAllAvailable()).thenReturn(products);

        servlet.doGet(request, response);

        verify(mockProductDAO).findAllAvailable();
        verify(request).setAttribute("products", products);
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoGetThrowsServletExceptionWhenDaoFails() {
        when(mockProductDAO.findAllAvailable()).thenThrow(new RuntimeException("Database error"));

        assertThrows(ServletException.class, () -> servlet.doGet(request, response));
    }

    @Test
    public void testDoPostDelegatesToDoGet() throws Exception {
        List<Product> products = new ArrayList<>();
        when(mockProductDAO.findAllAvailable()).thenReturn(products);

        servlet.doPost(request, response);

        verify(mockProductDAO).findAllAvailable();
        verify(request).setAttribute("products", products);
        verify(dispatcher).forward(request, response);
    }
}
