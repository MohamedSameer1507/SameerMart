package com.sameer.sameermart.service;

import com.sameer.sameermart.model.CartItem;

import java.util.List;

public interface CartService {
    List<CartItem> getCart(int buyerId);
    void addItem(int buyerId, int productId, int quantity);
    void updateQuantity(int buyerId, int productId, int quantity);
    void removeItem(int buyerId, int productId);
    void clearCart(int buyerId);
}
