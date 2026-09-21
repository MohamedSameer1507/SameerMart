package com.sameer.sameermart.cart;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CartDAO {
    private final NamedParameterJdbcTemplate jdbc;

    public CartDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<CartItem> findByUserId(long userId) {
        return jdbc.query("""
                SELECT p.id AS product_id, p.name, p.category, p.image_url, p.price,
                       c.quantity, p.stock_qty
                FROM cart_items c JOIN products p ON p.id = c.product_id
                WHERE c.user_id = :userId ORDER BY c.id
                """, Map.of("userId", userId), (rs, row) -> new CartItem(
                rs.getLong("product_id"), rs.getString("name"), rs.getString("category"),
                rs.getString("image_url"), rs.getLong("price"), rs.getInt("quantity"), rs.getInt("stock_qty")));
    }

    public Optional<Integer> findQuantity(long userId, long productId) {
        return jdbc.query("SELECT quantity FROM cart_items WHERE user_id = :userId AND product_id = :productId",
                Map.of("userId", userId, "productId", productId),
                (rs, row) -> rs.getInt("quantity")).stream().findFirst();
    }

    public void insert(long userId, long productId, int quantity) {
        jdbc.update("""
                INSERT INTO cart_items (user_id, product_id, quantity)
                VALUES (:userId, :productId, :quantity)
                """, Map.of("userId", userId, "productId", productId, "quantity", quantity));
    }

    public void updateQuantity(long userId, long productId, int quantity) {
        jdbc.update("""
                UPDATE cart_items SET quantity = :quantity
                WHERE user_id = :userId AND product_id = :productId
                """, Map.of("userId", userId, "productId", productId, "quantity", quantity));
    }

    public void remove(long userId, long productId) {
        jdbc.update("DELETE FROM cart_items WHERE user_id = :userId AND product_id = :productId",
                Map.of("userId", userId, "productId", productId));
    }

    public void clear(long userId) {
        jdbc.update("DELETE FROM cart_items WHERE user_id = :userId", Map.of("userId", userId));
    }
}
