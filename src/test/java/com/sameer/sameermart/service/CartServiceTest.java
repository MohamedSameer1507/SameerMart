package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.CartDAO;
import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.CartItem;
import com.sameer.sameermart.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

public class CartServiceTest {

    private CartDAO mockCartDAO;
    private ProductDAO mockProductDAO;
    private CartService cartService;

    @Before
    public void setUp() {
        mockCartDAO = mock(CartDAO.class);
        mockProductDAO = mock(ProductDAO.class);
        cartService = new CartServiceImpl(mockCartDAO, mockProductDAO);
    }

    @Test
    public void testGetCartItems() {
        when(mockCartDAO.getCartItems(1)).thenReturn(List.of(new CartItem()));
        List<CartItem> items = cartService.getCart(1);
        Assert.assertEquals(1, items.size());
    }

    @Test
    public void testAddItemSuccess() {
        Product p = new Product(10, "Phone", "desc", "Tech", new BigDecimal("500.00"), 10);
        when(mockProductDAO.findById(10)).thenReturn(p);

        cartService.addItem(1, 10, 2);
        verify(mockCartDAO).addItem(1, 10, 2);
    }

    @Test(expected = ConflictException.class)
    public void testAddItemExceedsStockThrowsConflict() {
        Product p = new Product(10, "Phone", "desc", "Tech", new BigDecimal("500.00"), 2);
        when(mockProductDAO.findById(10)).thenReturn(p);

        cartService.addItem(1, 10, 5);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testAddItemProductNotFoundThrowsException() {
        when(mockProductDAO.findById(99)).thenReturn(null);
        cartService.addItem(1, 99, 1);
    }

    @Test(expected = ValidationException.class)
    public void testAddItemZeroQuantityThrowsValidation() {
        cartService.addItem(1, 10, 0);
    }

    @Test
    public void testUpdateQuantityZeroRemovesItem() {
        cartService.updateQuantity(1, 10, 0);
        verify(mockCartDAO).removeItem(1, 10);
    }
}
