package com.sameer.sameermart.dao;

import com.sameer.sameermart.config.DatabaseConfig;
import com.sameer.sameermart.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    private static final Logger logger =
            LoggerFactory.getLogger(ReviewDAO.class);

    // =====================================================
    // HAS THE BUYER PURCHASED THE PRODUCT (ANY STATUS)?
    // =====================================================

    public boolean hasAnyOrderForProduct(int buyerId, int productId) {
        String sql = """
                SELECT COUNT(*) AS purchase_count
                FROM order_items oi
                JOIN orders o ON oi.order_id = o.id
                WHERE o.buyer_id = ?
                  AND oi.product_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, buyerId);
            statement.setInt(2, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("purchase_count") > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking any purchase order: {}", e.getMessage(), e);
        }
        return false;
    }

    // =====================================================
    // HAS THE BUYER PURCHASED THE PRODUCT (DELIVERED STATUS)?
    // =====================================================

    public boolean hasPurchasedProduct(
            int buyerId,
            int productId
    ) {

        String sql = """
                SELECT COUNT(*) AS purchase_count
                FROM order_items oi
                JOIN orders o ON oi.order_id = o.id
                WHERE o.buyer_id = ?
                  AND oi.product_id = ?
                  AND o.status = 'DELIVERED'
                """;

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, buyerId);
            statement.setInt(2, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return resultSet.getInt(
                            "purchase_count"
                    ) > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Error checking purchase eligibility: {}", e.getMessage(), e);
        }

        return false;
    }

    // =====================================================
    // HAS THE BUYER ALREADY REVIEWED THE PRODUCT?
    // =====================================================

    public boolean hasReviewByBuyer(
            int buyerId,
            int productId
    ) {

        String sql = """
                SELECT COUNT(*) AS review_count
                FROM reviews
                WHERE buyer_id = ?
                  AND product_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, buyerId);
            statement.setInt(2, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return resultSet.getInt(
                            "review_count"
                    ) > 0;
                }
            }

        } catch (SQLException e) {
            logger.error("Error checking existing review: {}", e.getMessage(), e);
        }

        return false;
    }

    // =====================================================
    // ADD REVIEW (WITH PURCHASE + DUPLICATE VALIDATION)
    // =====================================================

    public Review addReview(
            int buyerId,
            int productId,
            int rating,
            String reviewText
    ) throws Exception {

        String insertSql = """
                INSERT INTO reviews
                (product_id, buyer_id, rating, review_text)
                VALUES (?, ?, ?, ?)
                """;

        String selectCreatedSql = """
                SELECT r.id,
                       r.product_id,
                       r.buyer_id,
                       r.rating,
                       r.review_text,
                       r.created_at,
                       u.name AS buyer_name,
                       u.email AS buyer_email
                FROM reviews r
                JOIN users u ON r.buyer_id = u.id
                WHERE r.id = ?
                """;

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5."
            );
        }

        if (!hasAnyOrderForProduct(buyerId, productId)) {
            throw new IllegalStateException(
                    "You must purchase this product before reviewing it."
            );
        }

        if (!hasPurchasedProduct(buyerId, productId)) {
            throw new IllegalStateException(
                    "You can only review products from orders that have been DELIVERED."
            );
        }

        if (hasReviewByBuyer(buyerId, productId)) {
            throw new IllegalStateException(
                    "You have already reviewed this product."
            );
        }

        String safeText = (reviewText != null)
                ? reviewText.trim()
                : "";

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection();

                PreparedStatement insertStatement =
                        connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
        ) {

            insertStatement.setInt(1, productId);
            insertStatement.setInt(2, buyerId);
            insertStatement.setInt(3, rating);
            insertStatement.setString(4, safeText.isEmpty() ? null : safeText);

            int inserted =
                    insertStatement.executeUpdate();

            if (inserted == 0) {
                return null;
            }

            int generatedId;

            try (ResultSet keys =
                         insertStatement.getGeneratedKeys()) {

                if (keys.next()) {

                    generatedId = keys.getInt(1);

                } else {

                    throw new SQLException(
                            "Failed to retrieve generated review ID."
                    );
                }
            }

            try (PreparedStatement selectStatement =
                         connection.prepareStatement(selectCreatedSql)) {

                selectStatement.setInt(1, generatedId);

                try (ResultSet resultSet =
                             selectStatement.executeQuery()) {

                    if (resultSet.next()) {

                        return mapReview(resultSet);
                    }
                }
            }

            return null;

        } catch (SQLException e) {
            logger.error("Error adding review: {}", e.getMessage(), e);
            throw new IllegalStateException(
                    "Unable to submit review. Please try again."
            );
        }
    }

    // =====================================================
    // FIND REVIEWS BY PRODUCT
    // =====================================================

    public List<Review> findByProductId(
            int productId
    ) {

        List<Review> reviews =
                new ArrayList<>();

        String sql = """
                SELECT r.id,
                       r.product_id,
                       r.buyer_id,
                       r.rating,
                       r.review_text,
                       r.created_at,
                       u.name AS buyer_name,
                       u.email AS buyer_email
                FROM reviews r
                JOIN users u ON r.buyer_id = u.id
                WHERE r.product_id = ?
                ORDER BY r.id DESC
                """;

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    reviews.add(mapReview(resultSet));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding reviews: {}", e.getMessage(), e);
        }

        return reviews;
    }

    // =====================================================
    // AVERAGE RATING FOR PRODUCT
    // =====================================================

    public double getAverageRating(
            int productId
    ) {

        String sql = """
                SELECT AVG(rating) AS average_rating
                FROM reviews
                WHERE product_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    double average =
                            resultSet.getDouble("average_rating");

                    return resultSet.wasNull()
                            ? 0.0
                            : average;
                }
            }

        } catch (SQLException e) {
            logger.error("Error calculating average rating: {}", e.getMessage(), e);
        }

        return 0.0;
    }

    // =====================================================
    // REVIEW COUNT FOR PRODUCT
    // =====================================================

    public int getReviewCount(
            int productId
    ) {

        String sql = """
                SELECT COUNT(*) AS review_count
                FROM reviews
                WHERE product_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConfig.getDataSource().getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return resultSet.getInt("review_count");
                }
            }

        } catch (SQLException e) {
            logger.error("Error counting reviews: {}", e.getMessage(), e);
        }

        return 0;
    }

    // =====================================================
    // MAP REVIEW
    // =====================================================

    private Review mapReview(
            ResultSet resultSet
    ) throws SQLException {

        Review review = new Review(
                resultSet.getInt("id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("buyer_id"),
                resultSet.getInt("rating"),
                resultSet.getString("review_text"),
                resultSet.getTimestamp("created_at")
        );

        try {

            review.setBuyerName(
                    resultSet.getString("buyer_name")
            );

            review.setBuyerEmail(
                    resultSet.getString("buyer_email")
            );

        } catch (SQLException ignored) {
        }

        return review;
    }
}