package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductDAO mockProductDAO;
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        mockProductDAO = mock(ProductDAO.class);
        productService = new ProductServiceImpl(mockProductDAO);
    }

    @Test
    public void testGetProductByIdFound() {
        Product p = new Product(10, "Widget", "desc", "Cat", new BigDecimal("10.00"), 5);
        when(mockProductDAO.findById(10)).thenReturn(p);

        Product result = productService.getProductById(10);
        assertNotNull(result);
        assertEquals("Widget", result.getName());
    }

    @Test
    public void testGetProductByIdNotFoundThrowsException() {
        when(mockProductDAO.findById(999)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999));
    }

    @Test
    public void testGetAllActiveProducts() {
        Product p = new Product(1, "Item", "desc", "Cat", new BigDecimal("20.00"), 2);
        when(mockProductDAO.findAllActive()).thenReturn(List.of(p));

        List<Product> products = productService.getAllActiveProducts();
        assertEquals(1, products.size());
    }

    @Test
    public void testGetProductsByCategory() {
        when(mockProductDAO.findByCategory("Books")).thenReturn(List.of(new Product()));
        List<Product> list = productService.getProductsByCategory("Books");
        assertEquals(1, list.size());
    }

    @Test
    public void testSearchProducts() {
        when(mockProductDAO.searchProducts("test")).thenReturn(List.of(new Product()));
        List<Product> list = productService.searchProducts("test");
        assertEquals(1, list.size());
    }

    @Test
    public void testCreateProductInvalidPriceThrowsValidation() {
        Product p = new Product(0, 1, "Item", "desc", "Cat", new BigDecimal("-5.00"), 10, "img.jpg", null);
        assertThrows(ValidationException.class, () -> productService.createProduct(p));
    }
}
