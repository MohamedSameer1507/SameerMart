<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.Order" %>
<%@ page import="com.sameer.sameermart.model.OrderItem" %>
<%@ page import="com.sameer.sameermart.model.Product" %>
<%@ page import="com.sameer.sameermart.dao.OrderDAO" %>
<%@ page import="com.sameer.sameermart.dao.CartDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sameer.sameermart.util.HtmlUtil" %>

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

    List<Order> orders = (List<Order>) request.getAttribute("orders");
    if (orders == null) {
        OrderDAO orderDAO = new OrderDAO();
        orders = orderDAO.findOrdersByBuyerId(user.getId());
    }

    CartDAO cartDAO = new CartDAO();
    int cartCount = cartDAO.getCartItems(user.getId()).size();

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Orders - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261010_2">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat-widget.css?v=20261010_2">
</head>
<body>

    <!-- Navigation Bar -->
    <header class="navbar">
        <a href="${pageContext.request.contextPath}/buyer/marketplace" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/buyer/marketplace" class="nav-link">Marketplace</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/wishlist" class="nav-link">Wishlist</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/cart" class="nav-link">My Cart (<%= cartCount %>)</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/orders" class="nav-link active">My Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="seller-page">
        <div class="seller-container">

            <section class="page-hero">
                <div class="page-hero-intro">
                    <span class="eyebrow">📦 ORDER TRACKING & HISTORY</span>
                    <h1>My Placed <span class="highlight">Orders</span></h1>
                    <p>Track delivery progression, order references, verified reviews, and past purchase history.</p>
                </div>
                <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-primary-button">
                    Browse Marketplace →
                </a>
            </section>

            <% if (orders == null || orders.isEmpty()) { %>
                <div class="empty-state">
                    <div style="font-size: 3.5rem; margin-bottom: 1.2rem;">📦</div>
                    <h2>No Orders Placed Yet</h2>
                    <p>
                        You haven't placed any orders yet. Visit the marketplace to find fresh produce and products.
                    </p>
                    <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-primary-button">
                        Start Shopping
                    </a>
                </div>
            <% } else { %>
                <div style="display: flex; flex-direction: column; gap: 1.5rem;">
                    <% for (Order order : orders) { %>
                        <div class="products-panel" style="margin-bottom: 0;">
                            <div class="products-panel-header" style="background: var(--bg-surface-alt); border-bottom: 1px solid var(--border); padding: 1.2rem 1.6rem;">
                                <div style="display: flex; gap: 2rem; align-items: center; flex-wrap: wrap;">
                                    <div>
                                        <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; display: block; letter-spacing: 0.5px;">Order Placed</span>
                                        <strong style="color: var(--text-main);"><%= order.getCreatedAt() != null ? sdf.format(order.getCreatedAt()) : "Recently" %></strong>
                                    </div>
                                    <div>
                                        <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; display: block; letter-spacing: 0.5px;">Total Amount</span>
                                        <strong style="color: var(--primary); font-size: 1.1rem;">₹<%= order.getTotalAmount() %></strong>
                                    </div>
                                    <div>
                                        <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; display: block; letter-spacing: 0.5px; margin-bottom: 3px;">Status</span>
                                        <%
                                            String oStatus = order.getStatus() != null ? order.getStatus().trim().toUpperCase() : "CONFIRMED";
                                            String oStatusClass = "status-" + oStatus.toLowerCase();
                                        %>
                                        <span class="status-badge <%= oStatusClass %>"><%= HtmlUtil.escape(oStatus) %></span>
                                    </div>
                                </div>
                                <div>
                                    <a href="${pageContext.request.contextPath}/buyer/order?id=<%= order.getId() %>" class="btn-card-action btn-secondary" style="font-size: 0.82rem; padding: 6px 14px;">
                                        View Details #<%= order.getId() %> →
                                    </a>
                                </div>
                            </div>

                            <div class="products-table-wrapper" style="padding: 1rem 1.5rem;">
                                <table class="products-table">
                                    <thead>
                                        <tr>
                                            <th>Product Name</th>
                                            <th>Unit Price</th>
                                            <th>Quantity</th>
                                            <th>Subtotal</th>
                                            <th style="text-align: right;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% if (order.getItems() != null) {
                                            for (OrderItem item : order.getItems()) {
                                                Product p = item.getProduct();
                                                String pName = p != null ? p.getName() : "Product #" + item.getProductId();
                                                String pImg = (p != null && p.getImageUrl() != null) ? p.getImageUrl() : "default-product.svg";
                                                String imgSrc = pImg.startsWith("default-") ? request.getContextPath() + "/images/" + pImg : request.getContextPath() + "/images/products/" + pImg;
                                        %>
                                            <tr>
                                                <td>
                                                    <div style="display: flex; align-items: center; gap: 10px;">
                                                        <img src="<%= imgSrc %>" 
                                                             alt="<%= HtmlUtil.escape(pName) %>" 
                                                             class="product-thumb"
                                                             style="width: 32px; height: 32px;"
                                                             onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                                                        <span><strong><%= HtmlUtil.escape(pName) %></strong></span>
                                                    </div>
                                                </td>
                                                <td>₹<%= item.getPrice() %></td>
                                                <td><%= item.getQuantity() %></td>
                                                <td><strong>₹<%= item.getSubtotal() %></strong></td>
                                                <td style="text-align: right;">
                                                    <% if ("DELIVERED".equalsIgnoreCase(oStatus)) { %>
                                                        <a href="${pageContext.request.contextPath}/buyer/product-details?id=<%= item.getProductId() %>#reviews" class="btn-status-action btn-status-deliver">
                                                            ★ Leave Review
                                                        </a>
                                                    <% } else { %>
                                                        <span style="font-size: 0.72rem; color: #888;">Review after delivery</span>
                                                    <% } %>
                                                </td>
                                            </tr>
                                        <% }} %>
                                    </tbody>
                                </table>
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
