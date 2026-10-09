package com.sameer.sameermart.ai;

import java.util.Locale;

/**
 * MockChatProvider provides canned, realistic answers for standard marketplace FAQ questions
 * without requiring outbound network connectivity or API keys.
 */
public class MockChatProvider implements ChatProvider {

    @Override
    public String getReply(String userMessage, String context) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Hello! How can I assist you with SameerMart today?";
        }

        String msg = userMessage.trim().toLowerCase(Locale.ROOT);

        // 1. How to order
        if (msg.contains("how to order") || msg.contains("place an order") || msg.contains("buy") || msg.contains("purchase") || msg.contains("ordering")) {
            return "To place an order on SameerMart: 1) Browse or search the marketplace for items. 2) Click 'Add to Cart'. 3) Visit your Cart to review items and quantities. 4) Proceed to Checkout and confirm your simulated mock payment. You will receive an immediate order confirmation!";
        }

        // 2. Payment is mock
        if (msg.contains("payment") || msg.contains("pay") || msg.contains("credit card") || msg.contains("debit") || msg.contains("upi") || msg.contains("price")) {
            if (msg.contains("price") && context != null && !context.isEmpty()) {
                return "Here is information on current products: " + context;
            }
            return "SameerMart uses a secure simulated mock payment flow for demonstration. No real credit card or bank details are charged. You can safely complete checkout using our simulated payment confirmation.";
        }

        // 3. Returns and refund policy
        if (msg.contains("return") || msg.contains("refund") || msg.contains("cancel") || msg.contains("exchange")) {
            return "Our return policy allows requests within 7 days of delivery for damaged or incorrect goods. Since SameerMart operates with independent sellers, please reach out via support or contact the seller through your order details.";
        }

        // 4. How sellers list products
        if (msg.contains("seller") || msg.contains("list product") || msg.contains("add product") || msg.contains("sell")) {
            return "To sell on SameerMart: 1) Register an account with the 'Seller' role. 2) Log in and navigate to your Seller Dashboard. 3) Click 'Add Product' to submit your product name, description, price, stock quantity, category, and an optional image. Once reviewed by an administrator, your product will be live!";
        }

        // 5. Categories available
        if (msg.contains("category") || msg.contains("categories") || msg.contains("what do you sell") || msg.contains("catalog")) {
            return "SameerMart offers a variety of product categories including Spices, Groceries, Electronics, Apparel, and Home Essentials. You can filter by category directly on the Marketplace page.";
        }

        // 6. How reviews work (only delivered orders)
        if (msg.contains("review") || msg.contains("rating") || msg.contains("rate") || msg.contains("star")) {
            return "Customer reviews and star ratings (1-5 stars) ensure quality across our marketplace. In accordance with platform policy, buyers can only submit a review for products from orders that have reached 'DELIVERED' status.";
        }

        // 7. Order status meanings
        if (msg.contains("status") || msg.contains("track") || msg.contains("pending") || msg.contains("confirmed") || msg.contains("shipped") || msg.contains("delivered")) {
            return "SameerMart orders progress through four main stages: 1) PENDING: Checkout initiated. 2) CONFIRMED: Payment simulated and order locked in. 3) SHIPPED: Seller has dispatched the package. 4) DELIVERED: Order has arrived and is ready for customer review.";
        }

        // 8. Contact / Support
        if (msg.contains("contact") || msg.contains("support") || msg.contains("help") || msg.contains("email") || msg.contains("phone")) {
            return "You can reach SameerMart support by emailing support@sameermart.com or opening a query in your account dashboard. Our student project help desk is active during review hours.";
        }

        // 9. Shipping & delivery
        if (msg.contains("shipping") || msg.contains("ship") || msg.contains("delivery") || msg.contains("dispatch") || msg.contains("how long")) {
            return "Standard delivery typically takes 2 to 4 business days depending on your location. Once your order status changes to 'SHIPPED', dispatch details can be viewed under 'My Orders'.";
        }

        // 10. Account / Registration
        if (msg.contains("register") || msg.contains("signup") || msg.contains("sign up") || msg.contains("account") || msg.contains("login") || msg.contains("role")) {
            return "You can create an account by clicking 'Register' at the top of the page. Choose 'Buyer' to shop or 'Seller' to list your own products. Passwords are securely hashed with BCrypt.";
        }

        // If context has products and query mentions product or search
        if (context != null && !context.isEmpty() && (msg.contains("product") || msg.contains("item") || msg.contains("search") || msg.contains("find") || msg.contains("available"))) {
            return "Here are some of our popular marketplace products: " + context + ". You can search and filter for these on our Marketplace page!";
        }

        // Fallback friendly response
        return "Welcome to SameerMart! I can help you with questions about ordering, mock payments, seller listings, product categories, shipping, reviews, and order tracking. How can I help you today?";
    }
}
