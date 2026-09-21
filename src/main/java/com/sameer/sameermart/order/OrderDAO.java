package com.sameer.sameermart.order;

import com.sameer.sameermart.cart.CartItem;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDAO {
    private final NamedParameterJdbcTemplate jdbc;

    public OrderDAO(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public long createOrder(long buyerId, long totalMinor) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("""
                INSERT INTO orders (buyer_id, status, total_amount)
                VALUES (:buyerId, 'CONFIRMED', :totalMinor)
                """, new MapSqlParameterSource(Map.of("buyerId", buyerId, "totalMinor", totalMinor)),
                keyHolder, new String[] {"id"});
        Number id = keyHolder.getKey();
        if (id == null) {
            throw new IllegalStateException("Order id was not generated.");
        }
        return id.longValue();
    }

    public void addItems(long orderId, List<CartItem> items) {
        for (CartItem item : items) {
            jdbc.update("""
                    INSERT INTO order_items (order_id, product_id, quantity, unit_price)
                    VALUES (:orderId, :productId, :quantity, :unitPrice)
                    """, Map.of("orderId", orderId, "productId", item.productId(),
                    "quantity", item.quantity(), "unitPrice", item.unitPriceMinor()));
        }
    }

    public boolean belongsToBuyer(long orderId, long buyerId) {
        Boolean exists = jdbc.queryForObject("""
                SELECT EXISTS(SELECT 1 FROM orders WHERE id = :orderId AND buyer_id = :buyerId)
                """, Map.of("orderId", orderId, "buyerId", buyerId), Boolean.class);
        return Boolean.TRUE.equals(exists);
    }
}
