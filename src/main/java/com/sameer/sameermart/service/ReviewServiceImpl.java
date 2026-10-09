package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ReviewDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Review;

import java.util.List;

public class ReviewServiceImpl implements ReviewService {

    private final ReviewDAO reviewDAO;

    public ReviewServiceImpl() {
        this.reviewDAO = new ReviewDAO();
    }

    public ReviewServiceImpl(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    @Override
    public Review submitReview(int buyerId, int productId, int rating, String reviewText) throws Exception {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }
        if (reviewText != null && reviewText.length() > 500) {
            throw new ValidationException("Review text must not exceed 500 characters");
        }

        try {
            return reviewDAO.addReview(buyerId, productId, rating, reviewText);
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("already reviewed")) {
                throw new ConflictException(e.getMessage());
            }
            throw new ValidationException(e.getMessage());
        }
    }

    @Override
    public List<Review> getProductReviews(int productId) {
        return reviewDAO.findByProductId(productId);
    }

    @Override
    public double getAverageRating(int productId) {
        return reviewDAO.getAverageRating(productId);
    }

    @Override
    public int getReviewCount(int productId) {
        return reviewDAO.getReviewCount(productId);
    }
}
