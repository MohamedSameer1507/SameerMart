package com.sameer.sameermart.service;

/**
 * ServiceFactory provides singleton service instances for controllers and servlets,
 * establishing clean separation of concerns and inversion of control.
 */
public final class ServiceFactory {

    private static final AuthService AUTH_SERVICE = new AuthServiceImpl();
    private static final ProductService PRODUCT_SERVICE = new ProductServiceImpl();
    private static final CartService CART_SERVICE = new CartServiceImpl();
    private static final OrderService ORDER_SERVICE = new OrderServiceImpl();
    private static final ReviewService REVIEW_SERVICE = new ReviewServiceImpl();
    private static final WishlistService WISHLIST_SERVICE = new WishlistServiceImpl();

    private ServiceFactory() {}

    public static AuthService getAuthService() {
        return AUTH_SERVICE;
    }

    public static ProductService getProductService() {
        return PRODUCT_SERVICE;
    }

    public static CartService getCartService() {
        return CART_SERVICE;
    }

    public static OrderService getOrderService() {
        return ORDER_SERVICE;
    }

    public static ReviewService getReviewService() {
        return REVIEW_SERVICE;
    }

    public static WishlistService getWishlistService() {
        return WISHLIST_SERVICE;
    }
}
