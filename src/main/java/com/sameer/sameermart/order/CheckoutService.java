package com.sameer.sameermart.order;

import com.sameer.sameermart.cart.Cart;
import com.sameer.sameermart.cart.CartItem;
import com.sameer.sameermart.cart.CartService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CheckoutService {
    private final CartService cartService;
    private final OrderDAO orderDAO;

    public CheckoutService(CartService cartService, OrderDAO orderDAO) {
        this.cartService = cartService;
        this.orderDAO = orderDAO;
    }

    public Cart checkoutCart(long buyerId) {
        Cart cart = cartService.cart(buyerId);
        if (cart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your cart is empty.");
        }
        return cart;
    }

    @Transactional
    public long confirmMockPayment(long buyerId) {
        Cart cart = checkoutCart(buyerId);
        validateCurrentStock(cart.items());
        long orderId = orderDAO.createOrder(buyerId, cart.totalMinor());
        orderDAO.addItems(orderId, cart.items());
        cartService.clear(buyerId);
        return orderId;
    }

    public boolean orderBelongsTo(long orderId, long buyerId) {
        return orderDAO.belongsToBuyer(orderId, buyerId);
    }

    private void validateCurrentStock(List<CartItem> items) {
        for (CartItem item : items) {
            if (item.quantity() > item.availableStock()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        item.name() + " no longer has sufficient stock.");
            }
        }
    }
}
