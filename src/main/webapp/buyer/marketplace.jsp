<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.Product" %>
<%@ page import="com.sameer.sameermart.dao.ProductDAO" %>
<%@ page import="com.sameer.sameermart.dao.CartDAO" %>
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

    List<Product> products = (List<Product>) request.getAttribute("products");
    if (products == null) {
        ProductDAO productDAO = new ProductDAO();
        products = productDAO.findAllAvailable();
    }

    CartDAO cartDAO = new CartDAO();
    int cartCount = cartDAO.getCartItems(user.getId()).size();

    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Marketplace - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20260828_5">
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

            <!-- Marketplace Header -->
            <section class="page-hero">
                <div class="page-hero-intro">
                    <span class="eyebrow">🛍️ VERIFIED MULTI-VENDOR MARKETPLACE</span>
                    <h1>Explore & Buy Direct with <span class="highlight">Confidence</span></h1>
                    <p>Discover fresh listings from verified sellers. Real-time inventory tracking, atomic checkout, and 24/7 AI assistance.</p>
                </div>
                <div class="seller-role-badge">
                    ROLE: <%= role %>
                </div>
            </section>

            <% if (products == null || products.isEmpty()) { %>
                <div class="empty-state">
                    <div style="font-size: 3.5rem; margin-bottom: 1.2rem;">🛍️</div>
                    <h2>No Products Available</h2>
                    <p>
                        There are no active listings available right now. Please check back later!
                    </p>
                    <a href="${pageContext.request.contextPath}/buyer/orders" class="seller-primary-button">
                        View My Orders
                    </a>
                </div>
            <% } else { %>
                <div class="product-grid">
                    <% for (Product p : products) {
                        String pImg = "default-product.svg";
                        try {
                            if (p.getImageUrl() != null && !p.getImageUrl().trim().isEmpty()) {
                                pImg = p.getImageUrl().trim();
                            }
                        } catch (Throwable ignored) {}
                        String imgSrc = HtmlUtil.resolveImageUrl(pImg, request.getContextPath());
                        boolean inStock = p.getQuantity() > 0;
                    %>
                        <div class="product-card">
                            <!-- Product Image -->
                            <div class="product-card-media">
                                <img src="<%= imgSrc %>"
                                     alt="<%= HtmlUtil.escape(p.getName()) %>"
                                     onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                            </div>

                            <!-- Product Info -->
                            <div class="product-card-body">
                                <div class="product-card-header">
                                    <span class="category-chip"><%= HtmlUtil.escape(p.getCategory()) %></span>
                                    <span class="product-card-id">#<%= p.getId() %></span>
                                </div>

                                <h3 class="product-card-title"><%= HtmlUtil.escape(p.getName()) %></h3>

                                <div class="product-card-price-row">
                                    <span class="product-card-price">₹<%= p.getPrice() %></span>
                                    <span class="product-card-unit">/ unit</span>
                                </div>

                                <p class="product-card-desc">
                                    <%= (p.getDescription() != null && !p.getDescription().trim().isEmpty()) ? HtmlUtil.escape(p.getDescription()) : "No detailed description provided by the seller." %>
                                </p>

                                <% if (inStock) { %>
                                    <span class="stock-pill in-stock">IN STOCK: <%= p.getQuantity() %></span>
                                <% } else { %>
                                    <span class="stock-pill out-of-stock">OUT OF STOCK</span>
                                <% } %>

                                <div class="product-card-actions">
                                    <a href="${pageContext.request.contextPath}/buyer/product-details?id=<%= p.getId() %>"
                                       class="btn-card-action btn-secondary">
                                        View Details
                                    </a>
                                    <% if (inStock) { %>
                                        <form action="${pageContext.request.contextPath}/buyer/cart" method="post" style="flex: 1; display: flex; margin: 0;">
                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="action" value="add">
                                            <input type="hidden" name="productId" value="<%= p.getId() %>">
                                            <input type="hidden" name="quantity" value="1">
                                            <input type="hidden" name="redirect" value="marketplace">
                                            <button type="submit" class="btn-card-action btn-primary" style="width: 100%;">
                                                + Add to Cart
                                            </button>
                                        </form>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    <% } %>
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