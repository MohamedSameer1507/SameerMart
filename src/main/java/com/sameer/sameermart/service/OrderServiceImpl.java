package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.OrderDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.UnauthorizedException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Order;
import com.sameer.sameermart.model.SellerOrderItem;
import com.sameer.sameermart.model.User;
import com.sameer.sameermart.servlet.OrderStatusServlet;

import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderDAO orderDAO;

    public OrderServiceImpl() {
        this.orderDAO = new OrderDAO();
    }

    public OrderServiceImpl(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public Order placeOrderFromCart(int buyerId) throws Exception {
        if (buyerId <= 0) {
            throw new ValidationException("Invalid buyer ID");
        }
        return orderDAO.createOrderFromCart(buyerId);
    }

    @Override
    public Order getOrderById(int orderId, int buyerId) {
        Order order = orderDAO.findOrderById(orderId, buyerId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found or unauthorized");
        }
        return order;
    }

    @Override
    public List<Order> getBuyerOrders(int buyerId) {
        return orderDAO.findOrdersByBuyerId(buyerId);
    }

    @Override
    public List<SellerOrderItem> getSellerOrders(int sellerId) {
        return orderDAO.findSellerOrders(sellerId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderDAO.findAllOrders();
    }

    @Override
    public void updateOrderStatus(int orderId, String newStatus, User actor) {
        if (actor == null) {
            throw new UnauthorizedException("Authentication required");
        }
        String role = actor.getRole();
        if (role == null || (!"SELLER".equalsIgnoreCase(role) && !"ADMIN".equalsIgnoreCase(role))) {
            throw new UnauthorizedException("Seller or Admin role required");
        }

        Order order = orderDAO.findOrderById(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }

        if ("SELLER".equalsIgnoreCase(role) && !orderDAO.isSellerForOrder(actor.getId(), orderId)) {
            throw new UnauthorizedException("You are not authorized to update this order");
        }

        String currentStatus = (order.getStatus() != null) ? order.getStatus().toUpperCase() : "CONFIRMED";
        String normalizedTarget = (newStatus != null) ? newStatus.trim().toUpperCase() : "";

        if (!OrderStatusServlet.isValidStatusTransition(currentStatus, normalizedTarget)) {
            throw new ConflictException("Cannot transition order from " + currentStatus + " to " + normalizedTarget);
        }

        boolean updated = orderDAO.updateOrderStatus(orderId, normalizedTarget);
        if (!updated) {
            throw new RuntimeException("Failed to update order status");
        }
    }
}
