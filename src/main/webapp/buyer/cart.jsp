<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.CartItem" %>
<%@ page import="com.sameer.sameermart.model.Product" %>
<%@ page import="com.sameer.sameermart.dao.CartDAO" %>
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

    List<CartItem> cartItems = (List<CartItem>) request.getAttribute("cartItems");
    BigDecimal cartTotal = (BigDecimal) request.getAttribute("cartTotal");

    if (cartItems == null || cartTotal == null) {
        CartDAO cartDAO = new CartDAO();
        cartItems = cartDAO.getCartItems(user.getId());
        cartTotal = cartDAO.getCartTotal(user.getId());
    }

    int itemCount = (cartItems != null) ? cartItems.size() : 0;

    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Shopping Cart - SameerMart</title>
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
                <li><a href="${pageContext.request.contextPath}/buyer/wishlist" class="nav-link">Wishlist</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/cart" class="nav-link active">My Cart (<%= itemCount %>)</a></li>
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

            <!-- Cart Header -->
            <section class="seller-header">
                <div class="seller-introduction">
                    <span class="eyebrow">ORDER REVIEW</span>
                    <h1>Shopping Cart</h1>
                    <p>Review items in your cart, adjust quantities, and proceed to checkout.</p>
                </div>
                <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-secondary-button">
                    ← Continue Shopping
                </a>
            </section>

            <% if (cartItems == null || cartItems.isEmpty()) { %>
                <div class="empty-state">
                    <div style="font-size: 3.5rem; margin-bottom: 1.2rem;">🛒</div>
                    <h2>Your Cart is Currently Empty</h2>
                    <p>
                        Looks like you haven't added any products to your cart yet. Explore our fresh marketplace listings!
                    </p>
                    <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-primary-button">
                        Browse Marketplace
                    </a>
                </div>
            <% } else { %>
                <div style="display: flex; gap: 2rem; align-items: flex-start; flex-wrap: wrap;">

                    <!-- Cart Items Table -->
                    <div style="flex: 2; min-width: 320px;">
                        <div class="products-panel">
                            <div class="products-panel-header">
                                <div>
                                    <h3>Cart Items (<%= itemCount %>)</h3>
                                    <p>Manage your product selections</p>
                                </div>
                                <form action="${pageContext.request.contextPath}/buyer/cart" method="post" onsubmit="return confirm('Clear entire cart?');" style="margin: 0;">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="action" value="clear">
                                    <button type="submit" class="action-btn delete-btn" style="height: 30px; padding: 4px 10px; font-size: 0.75rem;">
                                        Clear Cart
                                    </button>
                                </form>
                            </div>

                            <div class="products-table-wrapper">
                                <table class="products-table">
                                    <thead>
                                        <tr>
                                            <th>Product</th>
                                            <th>Price</th>
                                            <th>Quantity</th>
                                            <th>Subtotal</th>
                                            <th style="text-align: right;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (CartItem item : cartItems) {
                                            Product prod = item.getProduct();
                                            String pImg = "default-product.svg";
                                            try {
                                                if (prod != null && prod.getImageUrl() != null && !prod.getImageUrl().trim().isEmpty()) {
                                                    pImg = prod.getImageUrl().trim();
                                                }
                                            } catch (Throwable ignored) {}
                                            String imgSrc = pImg.startsWith("default-") ? request.getContextPath() + "/images/" + pImg : request.getContextPath() + "/images/products/" + pImg;
                                            String prodName = prod != null ? prod.getName() : "Product #" + item.getProductId();
                                            BigDecimal unitPrice = prod != null ? prod.getPrice() : BigDecimal.ZERO;
                                            int maxStock = prod != null ? prod.getQuantity() : 999;
                                        %>
                                            <tr>
                                                <td>
                                                    <div style="display: flex; align-items: center; gap: 12px;">
                                                        <img src="<%= imgSrc %>" 
                                                             alt="<%= HtmlUtil.escape(prodName) %>" 
                                                             class="product-thumb"
                                                             onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                                                        <div>
                                                            <strong style="color: var(--text-main);"><%= HtmlUtil.escape(prodName) %></strong>
                                                            <% if (prod != null && prod.getCategory() != null) { %>
                                                                <span class="category-chip" style="margin-top: 4px;"><%= HtmlUtil.escape(prod.getCategory()) %></span>
                                                            <% } %>
                                                        </div>
                                                    </div>
                                                </td>
                                                <td><strong style="color: var(--text-main);">₹<%= unitPrice %></strong></td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/buyer/cart" method="post" style="display: flex; align-items: center; gap: 6px; margin: 0;">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="action" value="update">
                                                        <input type="hidden" name="productId" value="<%= item.getProductId() %>">
                                                        <input type="number" 
                                                               name="quantity" 
                                                               value="<%= item.getQuantity() %>" 
                                                               min="1" 
                                                               max="<%= maxStock %>" 
                                                               class="quantity-stepper-input"
                                                               style="width: 64px; height: 32px; padding: 2px 6px; font-size: 0.9rem;" 
                                                               required>
                                                        <button type="submit" class="action-btn" title="Update quantity">
                                                            Update
                                                        </button>
                                                    </form>
                                                </td>
                                                <td><strong style="color: var(--primary);">₹<%= item.getSubtotal() %></strong></td>
                                                <td style="text-align: right;">
                                                    <form action="${pageContext.request.contextPath}/buyer/cart" method="post" style="display: inline; margin: 0;">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="action" value="remove">
                                                        <input type="hidden" name="productId" value="<%= item.getProductId() %>">
                                                        <button type="submit" class="action-btn delete-btn" title="Remove item">
                                                            ✕
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                    <!-- Order Summary Card -->
                    <div style="flex: 1; min-width: 280px; max-width: 380px;">
                        <div class="seller-stat-card" style="padding: 2rem;">
                            <span class="eyebrow">CHECKOUT SUMMARY</span>
                            <h3 style="font-size: 1.3rem; font-weight: 800; color: var(--text-main); margin: 0.4rem 0 1.2rem; border-bottom: 1px solid var(--border); padding-bottom: 0.8rem;">Order Details</h3>

                            <div style="display: flex; justify-content: space-between; margin-bottom: 0.8rem; font-size: 0.92rem;">
                                <span style="color: var(--text-muted);">Items Subtotal</span>
                                <strong style="color: var(--text-main);">₹<%= cartTotal %></strong>
                            </div>

                            <div style="display: flex; justify-content: space-between; margin-bottom: 1rem; font-size: 0.92rem;">
                                <span style="color: var(--text-muted);">Standard Delivery</span>
                                <strong style="color: var(--success);">FREE</strong>
                            </div>

                            <div style="display: flex; justify-content: space-between; align-items: baseline; border-top: 1px solid var(--border); padding-top: 1rem; margin-bottom: 1.6rem;">
                                <span style="font-weight: 800; font-size: 0.95rem; text-transform: uppercase; color: var(--text-muted); letter-spacing: 0.5px;">Total Amount</span>
                                <span style="font-size: 2rem; font-weight: 900; color: var(--primary); letter-spacing: -0.5px;">₹<%= cartTotal %></span>
                            </div>

                            <a href="${pageContext.request.contextPath}/buyer/checkout" class="primary-button" style="display: block; width: 100%; text-align: center; padding: 0.95rem; font-size: 1rem;">
                                Proceed to Checkout →
                            </a>

                            <div style="margin-top: 1.2rem; text-align: center;">
                                <a href="${pageContext.request.contextPath}/buyer/marketplace" style="font-size: 0.85rem; font-weight: 600; color: var(--text-muted);">
                                    ← Add More Products
                                </a>
                            </div>
                        </div>
                    </div>

                </div>
            <% } %>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2026 SameerMart. All rights reserved.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/chat-widget.js" defer></script>
</body>
</html>
