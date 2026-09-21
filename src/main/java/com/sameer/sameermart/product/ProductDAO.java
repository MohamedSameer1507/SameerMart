package com.sameer.sameermart.product;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAO {
    private final NamedParameterJdbcTemplate jdbc;

    public ProductDAO(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Product> findAvailable(String keyword, String category) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, seller_id, name, description, price, stock_qty, category, image_url
                FROM products WHERE stock_qty > 0
                """);
        Map<String, Object> parameters = new java.util.HashMap<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(name) LIKE :keyword OR LOWER(description) LIKE :keyword)");
            parameters.put("keyword", "%" + keyword.trim().toLowerCase() + "%");
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND category = :category");
            parameters.put("category", category.trim());
        }
        sql.append(" ORDER BY created_at DESC, id DESC");
        return jdbc.query(sql.toString(), parameters, (rs, row) -> mapProduct(rs));
    }

    public List<String> findCategories() {
        return jdbc.queryForList("SELECT DISTINCT category FROM products ORDER BY category", Map.of(), String.class);
    }

    public Optional<Product> findAvailableById(long id) {
        return jdbc.query("""
                SELECT id, seller_id, name, description, price, stock_qty, category, image_url
                FROM products WHERE id = :id AND stock_qty > 0
                """, Map.of("id", id), (rs, row) -> mapProduct(rs)).stream().findFirst();
    }

    private Product mapProduct(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Product(rs.getLong("id"), rs.getLong("seller_id"), rs.getString("name"),
                rs.getString("description"), rs.getLong("price"), rs.getInt("stock_qty"),
                rs.getString("category"), rs.getString("image_url"));
    }
}
