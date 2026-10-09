package com.sameer.sameermart.service;

import com.sameer.sameermart.model.Order;
import com.sameer.sameermart.model.SellerOrderItem;
import com.sameer.sameermart.model.User;

import java.util.List;

public interface OrderService {
    Order placeOrderFromCart(int buyerId) throws Exception;
    Order getOrderById(int orderId, int buyerId);
    List<Order> getBuyerOrders(int buyerId);
    List<SellerOrderItem> getSellerOrders(int sellerId);
    List<Order> getAllOrders();
    void updateOrderStatus(int orderId, String newStatus, User actor);
}
