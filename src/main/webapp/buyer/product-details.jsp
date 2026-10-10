<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.Product" %>
<%@ page import="com.sameer.sameermart.model.Review" %>
<%@ page import="com.sameer.sameermart.dao.ProductDAO" %>
<%@ page import="com.sameer.sameermart.dao.CartDAO" %>
<%@ page import="com.sameer.sameermart.dao.ReviewDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sameer.sameermart.util.HtmlUtil" %>
<%@ page import="com.sameer.sameermart.util.CsrfUtil" %>

<%
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please+login+first");
        return;
    }

    String role = user.getRole();
    if (role == null || !"BUYER".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Buyer+access+required");
        return;
    }

    CsrfUtil.getOrCreateToken(session);

    Product product = (Product) request.getAttribute("product");
    if (product == null) {
        String idText = request.getParameter("id");
        if (idText != null && !idText.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idText.trim());
                ProductDAO productDAO = new ProductDAO();
                product = productDAO.findById(id);
            } catch (Exception ignored) {}
        }
    }

    if (product == null) {
        response.sendRedirect(request.getContextPath() + "/buyer/marketplace?error=Product+not+found");
        return;
    }

    CartDAO cartDAO = new CartDAO();
    int cartCount = cartDAO.getCartItems(user.getId()).size();

    List<Review> reviews = (List<Review>) request.getAttribute("reviews");
    int reviewCount = 0;
    double avgRating = 0.0;
    boolean hasPurchased = false;
    boolean hasReviewed = false;

    if (reviews == null) {
        ReviewDAO reviewDAO = new ReviewDAO();
        reviews = reviewDAO.findByProductId(product.getId());
        reviewCount = reviewDAO.getReviewCount(product.getId());
        avgRating = reviewDAO.getAverageRating(product.getId());
        hasPurchased = reviewDAO.hasPurchasedProduct(user.getId(), product.getId());
        hasReviewed = reviewDAO.hasReviewByBuyer(user.getId(), product.getId());
    } else {
        Object countAttr = request.getAttribute("reviewCount");
        if (countAttr instanceof Integer) {
            reviewCount = (Integer) countAttr;
        }
        Object avgAttr = request.getAttribute("avgRating");
        if (avgAttr instanceof Double) {
            avgRating = (Double) avgAttr;
        }
        Object purchasedAttr = request.getAttribute("hasPurchased");
        if (purchasedAttr instanceof Boolean) {
            hasPurchased = (Boolean) purchasedAttr;
        }
        Object reviewedAttr = request.getAttribute("hasReviewed");
        if (reviewedAttr instanceof Boolean) {
            hasReviewed = (Boolean) reviewedAttr;
        }
    }

    if (reviews == null) {
        reviews = new java.util.ArrayList<>();
    }

    String success = request.getParameter("success");
    String error = request.getParameter("error");

    String pImg = "default-product.svg";
    try {
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            pImg = product.getImageUrl().trim();
        }
    } catch (Throwable ignored) {}
    String imgSrc = pImg.startsWith("default-") ? request.getContextPath() + "/images/" + pImg : request.getContextPath() + "/images/products/" + pImg;
    boolean inStock = product.getQuantity() > 0;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= HtmlUtil.escape(product.getName()) %> - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20260828_4">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat-widget.css">
</head>
<body>

    <!-- Navigation Bar -->
    <header class="navbar">
        <a href="${pageContext.request.contextPath}/buyer/marketplace" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/buyer/marketplace" class="nav-link active">Marketplace</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/wishlist" class="nav-link">Wishlist</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/cart" class="nav-link">My Cart (<%= cartCount %>)</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/orders" class="nav-link">My Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="page">
        <div class="seller-container" style="max-width: 900px; width: 100%;">

            <% if (success != null && !success.trim().isEmpty()) {
                String safeSuccess = HtmlUtil.escape(success);
            %>
                <div class="message success"><%= safeSuccess %></div>
            <% } %>

            <% if (error != null && !error.trim().isEmpty()) {
                String safeError = HtmlUtil.escape(error);
            %>
                <div class="message error"><%= safeError %></div>
            <% } %>

            <div class="product-details-container">

                <!-- Product Image Section -->
                <div class="product-details-media">
                    <img src="<%= imgSrc %>"
                         alt="<%= HtmlUtil.escape(product.getName()) %>"
                         onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                </div>

                <!-- Product Info & Order Section -->
                <div class="product-details-info">
                    <span class="eyebrow">🛍️ <%= product.getCategory() != null ? HtmlUtil.escape(product.getCategory().toUpperCase()) : "GENERAL" %> &bull; ITEM #<%= product.getId() %></span>
                    <h1 style="font-size: 2.3rem; font-weight: 800; margin: 0.3rem 0 0.8rem; letter-spacing: -0.5px; color: var(--text-main);"><%= HtmlUtil.escape(product.getName()) %></h1>

                    <div style="display: flex; align-items: baseline; gap: 12px; margin-bottom: 1.2rem;">
                        <span style="font-size: 2.2rem; font-weight: 900; color: var(--primary); letter-spacing: -0.5px;">₹<%= product.getPrice() %></span>
                        <span style="color: var(--text-muted); font-size: 0.9rem;">/ unit</span>
                    </div>

                    <div style="margin-bottom: 1.5rem;">
                        <% if (inStock) { %>
                            <span class="stock-pill in-stock">IN STOCK: <%= product.getQuantity() %> UNITS AVAILABLE</span>
                        <% } else { %>
                            <span class="stock-pill out-of-stock">OUT OF STOCK</span>
                        <% } %>
                    </div>

                    <div style="margin-bottom: 1.8rem; border-top: 1px solid var(--border); border-bottom: 1px solid var(--border); padding: 1.2rem 0;">
                        <h4 style="font-size: 0.78rem; text-transform: uppercase; letter-spacing: 0.8px; color: var(--text-muted); margin-bottom: 0.5rem; font-weight: 700;">Product Description</h4>
                        <p style="font-size: 0.95rem; line-height: 1.6; color: var(--text-body);">
                            <%= (product.getDescription() != null && !product.getDescription().trim().isEmpty()) ? HtmlUtil.escape(product.getDescription()) : "No detailed description provided by the seller." %>
                        </p>
                    </div>

                    <% if (inStock) { %>
                        <form action="${pageContext.request.contextPath}/buyer/cart" method="post">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" value="<%= product.getId() %>">

                            <div style="display: flex; align-items: flex-end; gap: 14px; margin-bottom: 1.5rem;">
                                <div>
                                    <label for="quantity" style="display: block; font-size: 0.75rem; font-weight: 700; text-transform: uppercase; color: var(--text-muted); margin-bottom: 6px;">Quantity</label>
                                    <input type="number"
                                           id="quantity"
                                           name="quantity"
                                           value="1"
                                           min="1"
                                           max="<%= product.getQuantity() %>"
                                           class="quantity-stepper-input"
                                           required>
                                </div>

                                <div style="flex: 1;">
                                    <button type="submit" class="primary-button" style="padding: 0.85rem; width: 100%;">
                                        + Add to Cart
                                    </button>
                                </div>
                            </div>
                        </form>
                    <% } else { %>
                        <div class="message error" style="margin-bottom: 1.5rem;">
                            This item is currently out of stock. Please check back later!
                        </div>
                    <% } %>

                    <div style="margin-bottom: 1.5rem;">
                        <form action="${pageContext.request.contextPath}/buyer/wishlist" method="post" style="display: inline;">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" value="<%= product.getId() %>">
                            <input type="hidden" name="redirect" value="/buyer/product-details?id=<%= product.getId() %>">
                            <button type="submit" class="seller-secondary-button" style="width: 100%; text-align: center; padding: 0.75rem 1rem; font-size: 0.88rem; cursor: pointer;">
                                ❤️ Save to Wishlist
                            </button>
                        </form>
                    </div>

                    <div style="display: flex; gap: 15px; font-size: 0.88rem; font-weight: 700; color: var(--text-muted);">
                        <a href="${pageContext.request.contextPath}/buyer/marketplace">← Back to Marketplace</a>
                        <span>&bull;</span>
                        <a href="${pageContext.request.contextPath}/buyer/wishlist">View Wishlist →</a>
                        <span>&bull;</span>
                        <a href="${pageContext.request.contextPath}/buyer/cart">Go to Cart →</a>
                    </div>

                </div>

            </div>

            <!-- ===================================================== -->
            <!-- CUSTOMER REVIEWS & RATINGS                              -->
            <!-- ===================================================== -->

            <div class="products-panel" style="margin-top: 2rem;">
                <div class="products-panel-header">
                    <div>
                        <h3>Customer Reviews (<%= reviews.size() %>)</h3>
                        <p>Ratings and feedback from verified buyers.</p>
                    </div>
                </div>

                <% if (reviews.isEmpty()) { %>
                    <div class="empty-state" style="padding: 2.5rem 1.5rem;">
                        <h3 style="font-size: 1.1rem;">No Reviews Yet</h3>
                        <p>Be the first to share your experience with this product.</p>
                    </div>
                <% } else { %>
                    <!-- Rating Summary -->
                    <div class="reviews-summary">
                        <div>
                            <span class="eyebrow">AVERAGE RATING</span>
                            <div style="font-size: 2rem; font-weight: 900; letter-spacing: -1px; color: #000;">
                                <%= String.format("%.1f", avgRating) %> <span style="font-size: 0.9rem; font-weight: 700; color: #666;">/ 5</span>
                            </div>
                            <div class="review-stars">
                                <% for (int s = 1; s <= 5; s++) { %>
                                    <% if (s <= Math.round(avgRating)) { %>
                                        <span class="star-filled">★</span>
                                    <% } else { %>
                                        <span class="star-empty">★</span>
                                    <% } %>
                                <% } %>
                            </div>
                        </div>
                        <div style="text-align: right;">
                            <div style="font-size: 1.6rem; font-weight: 900; color: #000;"><%= reviewCount %></div>
                            <div style="font-size: 0.75rem; text-transform: uppercase; letter-spacing: 1px; color: #666; font-weight: 700;">Reviews</div>
                        </div>
                    </div>

                    <!-- Review List -->
                    <div>
                        <% for (Review rv : reviews) { %>
                            <div class="review-card">
                                <div class="review-card-header">
                                    <span class="reviewer-name"><%= HtmlUtil.escape(rv.getBuyerName() != null ? rv.getBuyerName() : "Verified Buyer") %></span>
                                    <span class="review-date"><%= rv.getCreatedAt() != null ? HtmlUtil.escape(rv.getCreatedAt().toString().substring(0, 10)) : "Recently" %></span>
                                </div>
                                <div class="review-stars" style="margin-bottom: 0.5rem;">
                                    <% for (int s = 1; s <= 5; s++) { %>
                                        <% if (s <= rv.getRating()) { %>
                                            <span class="star-filled">★</span>
                                        <% } else { %>
                                            <span class="star-empty">★</span>
                                        <% } %>
                                    <% } %>
                                </div>
                                <p class="review-text">
                                    <%= (rv.getReviewText() != null && !rv.getReviewText().trim().isEmpty()) ? HtmlUtil.escape(rv.getReviewText()) : "No written comment provided." %>
                                </p>
                            </div>
                        <% } %>
                    </div>
                <% } %>
            </div>

            <!-- Review Submission -->
            <% if (hasPurchased && !hasReviewed) { %>
                <div class="products-panel" style="margin-top: 2rem;">
                    <div class="products-panel-header">
                        <div>
                            <h3>Write a Review</h3>
                            <p>Only buyers who have purchased this item can rate it.</p>
                        </div>
                    </div>
                    <form action="${pageContext.request.contextPath}/buyer/review" method="post" style="padding: 1.8rem;">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="productId" value="<%= product.getId() %>">
                        <div class="form-group" style="max-width: 240px;">
                            <label for="rating">Your Rating</label>
                            <select id="rating" name="rating" required>
                                <option value="">Select a rating</option>
                                <option value="1">1 Star</option>
                                <option value="2">2 Stars</option>
                                <option value="3">3 Stars</option>
                                <option value="4">4 Stars</option>
                                <option value="5">5 Stars</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="reviewText">Your Review (optional)</label>
                            <textarea id="reviewText" name="reviewText" rows="4" maxlength="500" placeholder="Share what you liked or didn't like about this product..."></textarea>
                        </div>
                        <button type="submit" class="primary-button" style="max-width: 320px;">Submit Review</button>
                    </form>
                </div>
            <% } else if (hasPurchased && hasReviewed) { %>
                <div class="products-panel" style="margin-top: 2rem;">
                    <div class="products-panel-header">
                        <div>
                            <h3>Thank You</h3>
                            <p>You have already reviewed this product. Only one review per purchase is allowed.</p>
                        </div>
                    </div>
                </div>
            <% } else { %>
                <div class="products-panel" style="margin-top: 2rem;">
                    <div class="products-panel-header">
                        <div>
                            <h3>Leave a Review</h3>
                            <p>Purchase this item to unlock the ability to rate and review it.</p>
                        </div>
                    </div>
                </div>
            <% } %>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/chat-widget.js" defer></script>
</body>
</html>
