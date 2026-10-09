package com.sameer.sameermart.service;

import com.sameer.sameermart.dao.ReviewDAO;
import com.sameer.sameermart.exception.ConflictException;
import com.sameer.sameermart.exception.ValidationException;
import com.sameer.sameermart.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    private ReviewDAO mockReviewDAO;
    private ReviewService reviewService;

    @BeforeEach
    public void setUp() {
        mockReviewDAO = mock(ReviewDAO.class);
        reviewService = new ReviewServiceImpl(mockReviewDAO);
    }

    @Test
    public void testSubmitReviewSuccess() throws Exception {
        Review review = new Review(1, 10, 5, 5, "Superb!", null);
        when(mockReviewDAO.addReview(5, 10, 5, "Superb!")).thenReturn(review);

        Review result = reviewService.submitReview(5, 10, 5, "Superb!");
        assertNotNull(result);
        assertEquals(5, result.getRating());
    }

    @Test
    public void testSubmitReviewInvalidRatingThrowsValidation() throws Exception {
        assertThrows(ValidationException.class, () -> reviewService.submitReview(5, 10, 6, "Out of bounds"));
    }

    @Test
    public void testSubmitReviewAlreadyReviewedThrowsConflict() throws Exception {
        when(mockReviewDAO.addReview(5, 10, 4, "Second review"))
                .thenThrow(new IllegalStateException("You have already reviewed this product."));

        assertThrows(ConflictException.class, () -> reviewService.submitReview(5, 10, 4, "Second review"));
    }

    @Test
    public void testGetAverageRatingAndCount() {
        when(mockReviewDAO.getAverageRating(10)).thenReturn(4.5);
        when(mockReviewDAO.getReviewCount(10)).thenReturn(12);

        assertEquals(4.5, reviewService.getAverageRating(10), 0.001);
        assertEquals(12, reviewService.getReviewCount(10));
    }

    @Test
    public void testGetProductReviews() {
        when(mockReviewDAO.findByProductId(10)).thenReturn(List.of(new Review()));
        List<Review> list = reviewService.getProductReviews(10);
        assertEquals(1, list.size());
    }
}
