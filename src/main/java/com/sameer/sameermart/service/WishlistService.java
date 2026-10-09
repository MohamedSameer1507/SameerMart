package com.sameer.sameermart.service;

import com.sameer.sameermart.model.WishlistItem;

import java.util.List;

public interface WishlistService {
    List<WishlistItem> getWishlist(int buyerId);
    void addToWishlist(int buyerId, int productId);
    void removeFromWishlist(int buyerId, int productId);
    void moveToCart(int buyerId, int productId);
    boolean isWishlisted(int buyerId, int productId);
    int getWishlistCount(int buyerId);
}
