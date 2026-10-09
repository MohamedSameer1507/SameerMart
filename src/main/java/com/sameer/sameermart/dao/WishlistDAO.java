package com.sameer.sameermart.dao;

import com.sameer.sameermart.config.DatabaseConfig;
import com.sameer.sameermart.model.WishlistItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    private static final Logger logger = LoggerFactory.getLogger(WishlistDAO.class);

    public boolean addToWishlist(int buyerId, int productId) {
        if (isProductInWishlist(buyerId, productId)) {
            return true;
        }

        String sql = """
                INSERT INTO wishlist (buyer_id, product_id)
                VALUES (?, ?)
                """;

        try (
                Connection connection = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, buyerId);
            statement.setInt(2, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to add product ID: {} to wishlist for buyer ID: {}", productId, buyerId, e);
            return false;
        }
    }

    public boolean removeFromWishlist(int buyerId, int productId) {
        String sql = """
                DELETE FROM wishlist
                WHERE buyer_id = ? AND product_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, buyerId);
            statement.setInt(2, productId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to remove product ID: {} from wishlist for buyer ID: {}", productId, buyerId, e);
            return false;
        }
    }

    public boolean isProductInWishlist(int buyerId, int productId) {
        String sql = """
                SELECT 1 FROM wishlist
                WHERE buyer_id = ? AND product_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, buyerId);
            statement.setInt(2, productId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Failed to check wishlist status for buyer ID: {}, product ID: {}", buyerId, productId, e);
            return false;
        }
    }

    public List<WishlistItem> getWishlistByBuyerId(int buyerId) {
        List<WishlistItem> items = new ArrayList<>();
        String sql = """
                SELECT w.id, w.buyer_id, w.product_id, w.created_at,
                       p.name, p.price, p.description, p.category, p.image_url, p.quantity
                FROM wishlist w
                JOIN products p ON w.product_id = p.id
                WHERE w.buyer_id = ?
                ORDER BY w.created_at DESC
                """;

        try (
                Connection connection = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, buyerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem(
                            rs.getInt("id"),
                            rs.getInt("buyer_id"),
                            rs.getInt("product_id"),
                            rs.getTimestamp("created_at")
                    );
                    item.setProductName(rs.getString("name"));
                    item.setProductPrice(rs.getBigDecimal("price"));
                    item.setProductDescription(rs.getString("description"));
                    item.setProductCategory(rs.getString("category"));
                    item.setProductImageUrl(rs.getString("image_url"));
                    item.setProductQuantity(rs.getInt("quantity"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch wishlist for buyer ID: {}", buyerId, e);
        }
        return items;
    }

    public int getWishlistCount(int buyerId) {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE buyer_id = ?";
        try (
                Connection connection = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, buyerId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to count wishlist for buyer ID: {}", buyerId, e);
        }
        return 0;
    }

    public boolean clearWishlist(int buyerId) {
        String sql = "DELETE FROM wishlist WHERE buyer_id = ?";
        try (
                Connection connection = DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, buyerId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to clear wishlist for buyer ID: {}", buyerId, e);
            return false;
        }
    }
}
