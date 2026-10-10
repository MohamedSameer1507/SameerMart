<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.WishlistItem" %>
<%@ page import="com.sameer.sameermart.dao.WishlistDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
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

    List<WishlistItem> wishlistItems = (List<WishlistItem>) request.getAttribute("wishlistItems");
    if (wishlistItems == null) {
        WishlistDAO wishlistDAO = new WishlistDAO();
        wishlistItems = wishlistDAO.getWishlistByBuyerId(user.getId());
    }

    int itemCount = (wishlistItems != null) ? wishlistItems.size() : 0;
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Wishlist - SameerMart</title>
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
                <li><a href="${pageContext.request.contextPath}/buyer/marketplace" class="nav-link">Marketplace</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/wishlist" class="nav-link active">Wishlist (<%= itemCount %>)</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/cart" class="nav-link">My Cart</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/orders" class="nav-link">My Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="seller-page">
        <div class="seller-container">

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

            <!-- Wishlist Header -->
            <section class="page-hero">
                <div class="page-hero-intro">
                    <span class="eyebrow">❤️ SAVED FOR LATER</span>
                    <h1>My Personal <span class="highlight">Wishlist</span></h1>
                    <p>Save products to purchase later or move them directly to your shopping cart with one click.</p>
                </div>
                <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-secondary-button">
                    ← Browse Marketplace
                </a>
            </section>

            <% if (wishlistItems == null || wishlistItems.isEmpty()) { %>
                <div class="empty-state">
                    <div style="font-size: 3.5rem; margin-bottom: 1.2rem;">❤️</div>
                    <h2>Your Wishlist is Empty</h2>
                    <p>
                        You haven't saved any items to your wishlist yet. Discover products in our marketplace!
                    </p>
                    <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-primary-button">
                        Browse Marketplace
                    </a>
                </div>
            <% } else { %>
                <div class="products-panel">
                    <div class="products-panel-header">
                        <div>
                            <h3>Saved Products (<%= itemCount %>)</h3>
                            <p>Items saved for your future purchases</p>
                        </div>
                    </div>

                    <div class="products-table-wrapper">
                        <table class="products-table">
                            <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Price</th>
                                    <th>Status</th>
                                    <th style="text-align: right;">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (WishlistItem item : wishlistItems) {
                                    String pImg = item.getProductImageUrl();
                                    if (pImg == null || pImg.trim().isEmpty()) {
                                        pImg = "default-product.svg";
                                    }
                                    String imgSrc = pImg.startsWith("default-") 
                                            ? request.getContextPath() + "/images/" + pImg 
                                            : request.getContextPath() + "/images/products/" + pImg;
                                    String prodName = item.getProductName() != null ? item.getProductName() : "Product #" + item.getProductId();
                                    BigDecimal price = item.getProductPrice() != null ? item.getProductPrice() : BigDecimal.ZERO;
                                    boolean inStock = item.isInStock();
                                %>
                                    <tr>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 12px;">
                                                <img src="<%= imgSrc %>" 
                                                     alt="<%= HtmlUtil.escape(prodName) %>" 
                                                     class="product-thumb"
                                                     onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                                                <div>
                                                    <a href="${pageContext.request.contextPath}/buyer/product-details?id=<%= item.getProductId() %>" style="color: #000; text-decoration: none;">
                                                        <strong><%= HtmlUtil.escape(prodName) %></strong>
                                                    </a>
                                                    <% if (item.getProductCategory() != null) { %>
                                                        <span style="display: block; font-size: 0.75rem; color: #666;"><%= HtmlUtil.escape(item.getProductCategory()) %></span>
                                                    <% } %>
                                                </div>
                                            </div>
                                        </td>
                                        <td><strong>₹<%= price %></strong></td>
                                        <td>
                                            <% if (inStock) { %>
                                                <span class="status-badge" style="background: #e6f9ed; color: #1e7e34; border: 1px solid #1e7e34;">In Stock</span>
                                            <% } else { %>
                                                <span class="status-badge" style="background: #fee2e2; color: #dc2626; border: 1px solid #dc2626;">Out of Stock</span>
                                            <% } %>
                                        </td>
                                        <td style="text-align: right;">
                                            <div style="display: flex; gap: 8px; justify-content: flex-end;">
                                                <% if (inStock) { %>
                                                    <form action="${pageContext.request.contextPath}/buyer/wishlist" method="post" style="display: inline;">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="action" value="moveToCart">
                                                        <input type="hidden" name="productId" value="<%= item.getProductId() %>">
                                                        <button type="submit" class="action-btn" style="background: #000; color: #fff; font-size: 0.75rem; padding: 4px 10px;">
                                                            🛒 Move to Cart
                                                        </button>
                                                    </form>
                                                <% } %>

                                                <form action="${pageContext.request.contextPath}/buyer/wishlist" method="post" style="display: inline;" onsubmit="return confirm('Remove this item from wishlist?');">
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="action" value="remove">
                                                    <input type="hidden" name="productId" value="<%= item.getProductId() %>">
                                                    <button type="submit" class="action-btn delete-btn" style="font-size: 0.75rem; padding: 4px 10px;">
                                                        ✕ Remove
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
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
