package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.dao.WishlistDAO;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Product;
import com.sameer.sameermart.model.WishlistItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WishlistServiceTest {

    private WishlistDAO mockWishlistDAO;
    private ProductDAO mockProductDAO;
    private CartService mockCartService;
    private WishlistService wishlistService;

    @BeforeEach
    public void setUp() {
        mockWishlistDAO = mock(WishlistDAO.class);
        mockProductDAO = mock(ProductDAO.class);
        mockCartService = mock(CartService.class);
        wishlistService = new WishlistServiceImpl(mockWishlistDAO, mockProductDAO, mockCartService);
    }

    @Test
    public void testGetWishlistSuccess() {
        when(mockWishlistDAO.getWishlistByBuyerId(1)).thenReturn(List.of(new WishlistItem()));
        List<WishlistItem> items = wishlistService.getWishlist(1);
        assertEquals(1, items.size());
    }

    @Test
    public void testGetWishlistInvalidBuyerThrowsException() {
        assertThrows(ValidationException.class, () -> wishlistService.getWishlist(0));
    }

    @Test
    public void testAddToWishlistSuccess() {
        Product p = new Product(10, "Watch", "desc", "Accessories", new BigDecimal("199.99"), 5);
        when(mockProductDAO.findById(10)).thenReturn(p);

        wishlistService.addToWishlist(1, 10);
        verify(mockWishlistDAO).addToWishlist(1, 10);
    }

    @Test
    public void testAddToWishlistProductNotFoundThrowsException() {
        when(mockProductDAO.findById(99)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> wishlistService.addToWishlist(1, 99));
    }

    @Test
    public void testAddToWishlistInactiveProductThrowsException() {
        Product p = new Product(10, "Watch", "desc", "Accessories", new BigDecimal("199.99"), 5);
        p.setActive(false);
        when(mockProductDAO.findById(10)).thenReturn(p);

        assertThrows(ResourceNotFoundException.class, () -> wishlistService.addToWishlist(1, 10));
    }

    @Test
    public void testRemoveFromWishlistSuccess() {
        wishlistService.removeFromWishlist(1, 10);
        verify(mockWishlistDAO).removeFromWishlist(1, 10);
    }

    @Test
    public void testMoveToCartSuccess() {
        Product p = new Product(10, "Watch", "desc", "Accessories", new BigDecimal("199.99"), 5);
        when(mockProductDAO.findById(10)).thenReturn(p);

        wishlistService.moveToCart(1, 10);
        verify(mockCartService).addItem(1, 10, 1);
        verify(mockWishlistDAO).removeFromWishlist(1, 10);
    }

    @Test
    public void testMoveToCartOutOfStockThrowsException() {
        Product p = new Product(10, "Watch", "desc", "Accessories", new BigDecimal("199.99"), 0);
        when(mockProductDAO.findById(10)).thenReturn(p);

        assertThrows(ValidationException.class, () -> wishlistService.moveToCart(1, 10));
    }

    @Test
    public void testIsWishlistedAndCount() {
        when(mockWishlistDAO.isProductInWishlist(1, 10)).thenReturn(true);
        when(mockWishlistDAO.getWishlistCount(1)).thenReturn(3);

        assertTrue(wishlistService.isWishlisted(1, 10));
        assertFalse(wishlistService.isWishlisted(1, 11));
        assertEquals(3, wishlistService.getWishlistCount(1));
    }
}
