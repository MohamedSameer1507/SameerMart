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

    if (cartItems == null || cartItems.isEmpty()) {
        response.sendRedirect(request.getContextPath() + "/buyer/cart?error=Your+cart+is+empty");
        return;
    }

    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20260828_4">
</head>
<body>

    <!-- Navigation Bar -->
    <header class="navbar">
        <a href="${pageContext.request.contextPath}/buyer/marketplace" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/buyer/marketplace" class="nav-link">Marketplace</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/cart" class="nav-link">My Cart (<%= cartItems.size() %>)</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/orders" class="nav-link">My Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="seller-page">
        <div class="seller-container" style="max-width: 960px;">

            <% if (error != null && !error.trim().isEmpty()) {
                String safeError = HtmlUtil.escape(error);
            %>
                <div class="message error"><%= safeError %></div>
            <% } %>

            <section class="page-hero">
                <div class="page-hero-intro">
                    <span class="eyebrow">💳 ATOMIC CHECKOUT</span>
                    <h1>Confirm & Place <span class="highlight">Order</span></h1>
                    <p>Please review your purchase items, verified buyer profile, and shipping summary before finalizing.</p>
                </div>
                <a href="${pageContext.request.contextPath}/buyer/cart" class="seller-secondary-button">
                    ← Edit Cart
                </a>
            </section>

            <div style="display: flex; gap: 2rem; align-items: flex-start; flex-wrap: wrap;">

                <!-- Order Items Review Table -->
                <div style="flex: 1.8; min-width: 320px;">
                    <div class="products-panel">
                        <div class="products-panel-header">
                            <div>
                                <h3>Order Items</h3>
                                <p><%= cartItems.size() %> product<%= cartItems.size() != 1 ? "s" : "" %> selected</p>
                            </div>
                        </div>

                        <div class="products-table-wrapper">
                            <table class="products-table">
                                <thead>
                                    <tr>
                                        <th>Product</th>
                                        <th>Price</th>
                                        <th>Qty</th>
                                        <th style="text-align: right;">Subtotal</th>
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
                                    %>
                                        <tr>
                                            <td>
                                                <div style="display: flex; align-items: center; gap: 10px;">
                                                    <img src="<%= imgSrc %>" 
                                                         alt="<%= HtmlUtil.escape(prodName) %>" 
                                                         class="product-thumb"
                                                         style="width: 38px; height: 38px;"
                                                         onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                                                    <div>
                                                        <strong><%= HtmlUtil.escape(prodName) %></strong>
                                                        <% if (prod != null && prod.getCategory() != null) { %>
                                                            <span style="display: block; font-size: 0.72rem; color: #666;"><%= HtmlUtil.escape(prod.getCategory()) %></span>
                                                        <% } %>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>₹<%= unitPrice %></td>
                                            <td><%= item.getQuantity() %></td>
                                            <td style="text-align: right;"><strong>₹<%= item.getSubtotal() %></strong></td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Buyer Details & Place Order -->
                <div style="flex: 1.2; min-width: 280px;">
                    <div class="seller-stat-card" style="padding: 2rem;">
                        <span class="eyebrow">BUYER & SHIPPING</span>
                        <h3 style="font-size: 1.25rem; font-weight: 800; color: var(--text-main); margin: 0.3rem 0 1.2rem;">Customer Info</h3>

                        <div style="background: var(--bg-surface-alt); border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1.2rem; margin-bottom: 1.5rem; font-size: 0.9rem;">
                            <div style="margin-bottom: 0.6rem;">
                                <span style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block; font-weight: 700;">Account Name</span>
                                <strong style="color: var(--text-main);"><%= HtmlUtil.escape(user.getName()) %></strong>
                            </div>
                            <div style="margin-bottom: 0.6rem;">
                                <span style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block; font-weight: 700;">Email Address</span>
                                <span style="color: var(--text-body);"><%= HtmlUtil.escape(user.getEmail()) %></span>
                            </div>
                            <div>
                                <span style="color: var(--text-muted); font-size: 0.75rem; text-transform: uppercase; display: block; font-weight: 700; margin-bottom: 4px;">Account Type</span>
                                <span class="category-chip" style="font-size: 0.72rem; padding: 2px 8px;">BUYER VERIFIED</span>
                            </div>
                        </div>

                        <div style="border-top: 1px solid var(--border); padding-top: 1rem; margin-bottom: 1.5rem;">
                            <div style="display: flex; justify-content: space-between; margin-bottom: 0.6rem; font-size: 0.92rem;">
                                <span style="color: var(--text-muted);">Total Items</span>
                                <strong style="color: var(--text-main);"><%= cartItems.size() %> items</strong>
                            </div>
                            <div style="display: flex; justify-content: space-between; margin-bottom: 0.8rem; font-size: 0.92rem;">
                                <span style="color: var(--text-muted);">Delivery</span>
                                <strong style="color: var(--success);">FREE</strong>
                            </div>
                            <div style="display: flex; justify-content: space-between; align-items: baseline; border-top: 1px solid var(--border); padding-top: 1rem;">
                                <span style="font-weight: 800; font-size: 0.95rem; text-transform: uppercase; color: var(--text-muted); letter-spacing: 0.5px;">Total to Pay</span>
                                <span style="font-size: 2rem; font-weight: 900; color: var(--primary); letter-spacing: -0.5px;">₹<%= cartTotal %></span>
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/buyer/place-order" method="post" style="margin: 0;">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <button type="submit" 
                                    class="primary-button" 
                                    style="width: 100%; padding: 1rem; font-size: 1rem;"
                                    onclick="this.disabled=true; this.innerText='Processing Order...'; this.form.submit();">
                                Place Order Now (₹<%= cartTotal %>)
                            </button>
                        </form>
                    </div>
                </div>

            </div>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

</body>
</html>
