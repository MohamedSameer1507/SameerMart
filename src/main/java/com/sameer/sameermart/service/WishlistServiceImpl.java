package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.dao.WishlistDAO;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Product;
import com.sameer.sameermart.model.WishlistItem;

import java.util.List;

public class WishlistServiceImpl implements WishlistService {

    private final WishlistDAO wishlistDAO;
    private final ProductDAO productDAO;
    private final CartService cartService;

    public WishlistServiceImpl() {
        this(new WishlistDAO(), new ProductDAO(), new CartServiceImpl());
    }

    public WishlistServiceImpl(WishlistDAO wishlistDAO, ProductDAO productDAO, CartService cartService) {
        this.wishlistDAO = wishlistDAO;
        this.productDAO = productDAO;
        this.cartService = cartService;
    }

    @Override
    public List<WishlistItem> getWishlist(int buyerId) {
        if (buyerId <= 0) {
            throw new ValidationException("Invalid buyer ID");
        }
        return wishlistDAO.getWishlistByBuyerId(buyerId);
    }

    @Override
    public void addToWishlist(int buyerId, int productId) {
        if (buyerId <= 0) {
            throw new ValidationException("Invalid buyer ID");
        }
        if (productId <= 0) {
            throw new ValidationException("Invalid product ID");
        }
        Product product = productDAO.findById(productId);
        if (product == null || !product.isActive()) {
            throw new ResourceNotFoundException("Product not found or inactive");
        }
        wishlistDAO.addToWishlist(buyerId, productId);
    }

    @Override
    public void removeFromWishlist(int buyerId, int productId) {
        if (buyerId <= 0 || productId <= 0) {
            throw new ValidationException("Invalid buyer or product ID");
        }
        wishlistDAO.removeFromWishlist(buyerId, productId);
    }

    @Override
    public void moveToCart(int buyerId, int productId) {
        if (buyerId <= 0 || productId <= 0) {
            throw new ValidationException("Invalid buyer or product ID");
        }
        Product product = productDAO.findById(productId);
        if (product == null || !product.isActive()) {
            throw new ResourceNotFoundException("Product not found or inactive");
        }
        if (product.getQuantity() <= 0) {
            throw new ValidationException("Product is out of stock");
        }
        cartService.addItem(buyerId, productId, 1);
        wishlistDAO.removeFromWishlist(buyerId, productId);
    }

    @Override
    public boolean isWishlisted(int buyerId, int productId) {
        if (buyerId <= 0 || productId <= 0) {
            return false;
        }
        return wishlistDAO.isProductInWishlist(buyerId, productId);
    }

    @Override
    public int getWishlistCount(int buyerId) {
        if (buyerId <= 0) {
            return 0;
        }
        return wishlistDAO.getWishlistCount(buyerId);
    }
}
