package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.OrderDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.UnauthorizedException;
import com.sameer.sameermart.model.Order;
import com.sameer.sameermart.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderDAO mockOrderDAO;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        mockOrderDAO = mock(OrderDAO.class);
        orderService = new OrderServiceImpl(mockOrderDAO);
    }

    @Test
    public void testPlaceOrderFromCart() throws Exception {
        Order order = new Order(1, 5, new BigDecimal("100.00"), "CONFIRMED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.createOrderFromCart(5)).thenReturn(order);

        Order result = orderService.placeOrderFromCart(5);
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void testGetBuyerOrders() {
        when(mockOrderDAO.findOrdersByBuyerId(5)).thenReturn(List.of(new Order()));
        List<Order> orders = orderService.getBuyerOrders(5);
        assertEquals(1, orders.size());
    }

    @Test
    public void testUpdateOrderStatusSellerSuccess() {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");

        Order order = new Order(10, 5, new BigDecimal("50.00"), "CONFIRMED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrderById(10)).thenReturn(order);
        when(mockOrderDAO.isSellerForOrder(20, 10)).thenReturn(true);
        when(mockOrderDAO.updateOrderStatus(10, "SHIPPED")).thenReturn(true);

        orderService.updateOrderStatus(10, "SHIPPED", seller);
        verify(mockOrderDAO).updateOrderStatus(10, "SHIPPED");
    }

    @Test
    public void testUpdateOrderStatusBuyerThrowsUnauthorized() {
        User buyer = new User();
        buyer.setId(5);
        buyer.setRole("BUYER");

        assertThrows(UnauthorizedException.class, () -> orderService.updateOrderStatus(10, "SHIPPED", buyer));
    }

    @Test
    public void testUpdateOrderStatusInvalidTransitionThrowsConflict() {
        User seller = new User();
        seller.setId(20);
        seller.setRole("SELLER");

        Order order = new Order(10, 5, new BigDecimal("50.00"), "SHIPPED", new Timestamp(System.currentTimeMillis()));
        when(mockOrderDAO.findOrderById(10)).thenReturn(order);
        when(mockOrderDAO.isSellerForOrder(20, 10)).thenReturn(true);

        assertThrows(ConflictException.class, () -> orderService.updateOrderStatus(10, "PENDING", seller));
    }

    @Test
    public void testUpdateOrderStatusNotFoundThrowsException() {
        User admin = new User();
        admin.setId(1);
        admin.setRole("ADMIN");

        when(mockOrderDAO.findOrderById(99)).thenReturn(null);
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(99, "DELIVERED", admin));
    }
}
