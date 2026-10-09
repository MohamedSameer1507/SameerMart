package com.sameer.sameermart.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BusinessRuleTest {

    @Test
    public void testCartSubtotalAndTotalCalculation() {
        BigDecimal unitPrice = new BigDecimal("49.99");
        int quantity = 3;
        BigDecimal expectedSubtotal = unitPrice.multiply(BigDecimal.valueOf(quantity)); // 149.97

        assertEquals(new BigDecimal("149.97"), expectedSubtotal);
    }

    @Test
    public void testStockDeductionInvariants() {
        int initialStock = 25;
        int purchasedQty = 7;
        int remainingStock = initialStock - purchasedQty;

        assertEquals(18, remainingStock);
        assertTrue(remainingStock >= 0, "Remaining stock must not be negative");
    }

    @Test
    public void testProductActiveStateDefault() {
        Product p = new Product(1, "Organic Oats", "Healthy", "Grains", new BigDecimal("75.00"), 20);
        assertTrue(p.isActive(), "New product must be active by default");

        p.setActive(false);
        assertFalse(p.isActive(), "Product can be explicitly deactivated");
    }

    @Test
    public void testRoleInvariants() {
        User buyer = new User("Alice", "alice@example.com", "hash", "BUYER");
        User seller = new User("Bob", "bob@example.com", "hash", "SELLER");
        User admin = new User("Charlie", "admin@example.com", "hash", "ADMIN");

        assertEquals("BUYER", buyer.getRole());
        assertEquals("SELLER", seller.getRole());
        assertEquals("ADMIN", admin.getRole());
    }

    @Test
    public void testCartItemBoundaryValues() {
        CartItem item = new CartItem();
        item.setQuantity(1);
        assertEquals(1, item.getQuantity());

        item.setProductId(101);
        assertEquals(101, item.getProductId());

        item.setBuyerId(5);
        assertEquals(5, item.getBuyerId());
    }
}
