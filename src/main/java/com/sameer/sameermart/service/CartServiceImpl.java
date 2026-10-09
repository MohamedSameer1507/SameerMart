package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.CartDAO;
import com.sameer.sameermart.dao.ProductDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.ResourceNotFoundException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.CartItem;
import com.sameer.sameermart.model.Product;

import java.util.List;

public class CartServiceImpl implements CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartServiceImpl() {
        this.cartDAO = new CartDAO();
        this.productDAO = new ProductDAO();
    }

    public CartServiceImpl(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    @Override
    public List<CartItem> getCart(int buyerId) {
        if (buyerId <= 0) {
            throw new ValidationException("Invalid buyer ID");
        }
        return cartDAO.getCartItems(buyerId);
    }

    @Override
    public void addItem(int buyerId, int productId, int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }
        Product product = productDAO.findById(productId);
        if (product == null || !product.isActive()) {
            throw new ResourceNotFoundException("Product not found or unavailable");
        }
        if (product.getQuantity() < quantity) {
            throw new ConflictException("Insufficient stock available (" + product.getQuantity() + " remaining)");
        }
        cartDAO.addItem(buyerId, productId, quantity);
    }

    @Override
    public void updateQuantity(int buyerId, int productId, int quantity) {
        if (quantity <= 0) {
            removeItem(buyerId, productId);
            return;
        }
        Product product = productDAO.findById(productId);
        if (product == null || !product.isActive()) {
            throw new ResourceNotFoundException("Product not found or unavailable");
        }
        if (product.getQuantity() < quantity) {
            throw new ConflictException("Requested quantity exceeds available stock (" + product.getQuantity() + ")");
        }
        cartDAO.updateQuantity(buyerId, productId, quantity);
    }

    @Override
    public void removeItem(int buyerId, int productId) {
        cartDAO.removeItem(buyerId, productId);
    }

    @Override
    public void clearCart(int buyerId) {
        cartDAO.clearCart(buyerId);
    }
}
