package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductDAO mockProductDAO;
    private ProductService productService;

    @Before
    public void setUp() {
        mockProductDAO = mock(ProductDAO.class);
        productService = new ProductServiceImpl(mockProductDAO);
    }

    @Test
    public void testGetProductByIdFound() {
        Product p = new Product(10, "Widget", "desc", "Cat", new BigDecimal("10.00"), 5);
        when(mockProductDAO.findById(10)).thenReturn(p);

        Product result = productService.getProductById(10);
        Assert.assertNotNull(result);
        Assert.assertEquals("Widget", result.getName());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetProductByIdNotFoundThrowsException() {
        when(mockProductDAO.findById(999)).thenReturn(null);
        productService.getProductById(999);
    }

    @Test
    public void testGetAllActiveProducts() {
        Product p = new Product(1, "Item", "desc", "Cat", new BigDecimal("20.00"), 2);
        when(mockProductDAO.findAllActive()).thenReturn(List.of(p));

        List<Product> products = productService.getAllActiveProducts();
        Assert.assertEquals(1, products.size());
    }

    @Test
    public void testGetProductsByCategory() {
        when(mockProductDAO.findByCategory("Books")).thenReturn(List.of(new Product()));
        List<Product> list = productService.getProductsByCategory("Books");
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testSearchProducts() {
        when(mockProductDAO.searchProducts("test")).thenReturn(List.of(new Product()));
        List<Product> list = productService.searchProducts("test");
        Assert.assertEquals(1, list.size());
    }

    @Test(expected = ValidationException.class)
    public void testCreateProductInvalidPriceThrowsValidation() {
        Product p = new Product(0, 1, "Item", "desc", "Cat", new BigDecimal("-5.00"), 10, "img.jpg", null);
        productService.createProduct(p);
    }
}
