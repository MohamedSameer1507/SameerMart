package com.sameer.sameermart.cart;

import com.sameer.sameermart.product.Product;
import com.sameer.sameermart.product.ProductService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CartService {
    private final CartDAO cartDAO;
    private final ProductService productService;

    public CartService(CartDAO cartDAO, ProductService productService) {
        this.cartDAO = cartDAO;
        this.productService = productService;
    }

    public Cart cart(long userId) {
        return new Cart(cartDAO.findByUserId(userId));
    }

    @Transactional
    public void add(long userId, long productId, int quantity) {
        Product product = productService.product(productId);
        int newQuantity = cartDAO.findQuantity(userId, productId).orElse(0) + positive(quantity);
        validateQuantity(newQuantity, product.stockQuantity());
        if (cartDAO.findQuantity(userId, productId).isPresent()) {
            cartDAO.updateQuantity(userId, productId, newQuantity);
        } else {
            cartDAO.insert(userId, productId, newQuantity);
        }
    }

    @Transactional
    public void update(long userId, long productId, int quantity) {
        if (quantity <= 0) {
            cartDAO.remove(userId, productId);
            return;
        }
        Product product = productService.product(productId);
        validateQuantity(quantity, product.stockQuantity());
        cartDAO.updateQuantity(userId, productId, quantity);
    }

    @Transactional
    public void remove(long userId, long productId) {
        cartDAO.remove(userId, productId);
    }

    public void clear(long userId) {
        cartDAO.clear(userId);
    }

    public List<CartItem> items(long userId) {
        return cartDAO.findByUserId(userId);
    }

    private int positive(int quantity) {
        if (quantity < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least one.");
        }
        return quantity;
    }

    private void validateQuantity(int quantity, int stock) {
        if (quantity > stock) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested quantity exceeds available stock.");
        }
    }
}
