package com.sameer.sameermart.service;

import com.sameer.sameermart.model.Review;

import java.util.List;

public interface ReviewService {
    Review submitReview(int buyerId, int productId, int rating, String reviewText) throws Exception;
    List<Review> getProductReviews(int productId);
    double getAverageRating(int productId);
    int getReviewCount(int productId);
}
