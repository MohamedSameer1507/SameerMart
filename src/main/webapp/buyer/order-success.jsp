<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.Order" %>
<%@ page import="com.sameer.sameermart.model.OrderItem" %>
<%@ page import="com.sameer.sameermart.model.Product" %>
<%@ page import="com.sameer.sameermart.dao.OrderDAO" %>
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

    String orderIdText = request.getParameter("orderId");
    Order order = null;

    if (orderIdText != null && !orderIdText.trim().isEmpty()) {
        try {
            int orderId = Integer.parseInt(orderIdText.trim());
            OrderDAO orderDAO = new OrderDAO();
            order = orderDAO.findOrderById(orderId, user.getId());
        } catch (NumberFormatException ignored) {}
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmation - SameerMart</title>
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
                <li><a href="${pageContext.request.contextPath}/buyer/cart" class="nav-link">My Cart</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/orders" class="nav-link">My Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="seller-page">
        <div class="seller-container" style="max-width: 800px;">

            <!-- Success Banner Card -->
            <div class="seller-stat-card" style="padding: 2.8rem; text-align: center; display: block; margin-bottom: 2rem;">
                <div style="font-size: 3.8rem; margin-bottom: 0.8rem;">🎉</div>
                <span class="eyebrow">✅ PAYMENT & ORDER CONFIRMED</span>
                <h1 style="font-size: 2.4rem; font-weight: 800; margin: 0.4rem 0 0.8rem; letter-spacing: -0.8px;">Thank You for Your <span class="highlight">Order!</span></h1>
                <p style="color: var(--text-muted); max-width: 520px; margin: 0 auto 1.8rem; font-size: 1rem; line-height: 1.6;">
                    Your transaction completed successfully. Inventory was decremented atomically and sellers are preparing fulfillment.
                </p>

                <% if (order != null) { %>
                    <div style="display: inline-flex; gap: 2rem; background: var(--bg-surface-alt); border: 1px solid var(--border); border-radius: var(--radius-lg); padding: 1.2rem 2.2rem; margin-bottom: 1.5rem; text-align: left; flex-wrap: wrap;">
                        <div>
                            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; display: block; letter-spacing: 0.5px;">Order Reference</span>
                            <strong style="font-size: 1.15rem; color: var(--text-main);">#<%= order.getId() %></strong>
                        </div>
                        <div>
                            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; display: block; letter-spacing: 0.5px;">Order Date</span>
                            <span style="color: var(--text-body);"><%= order.getCreatedAt() != null ? sdf.format(order.getCreatedAt()) : "Today" %></span>
                        </div>
                        <div>
                            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; display: block; letter-spacing: 0.5px; margin-bottom: 3px;">Status</span>
                            <span class="status-badge status-confirmed"><%= HtmlUtil.escape(order.getStatus()) %></span>
                        </div>
                        <div>
                            <span style="font-size: 0.75rem; color: var(--text-muted); text-transform: uppercase; font-weight: 700; display: block; letter-spacing: 0.5px;">Total Paid</span>
                            <strong style="font-size: 1.25rem; color: var(--primary);">₹<%= order.getTotalAmount() %></strong>
                        </div>
                    </div>
                <% } %>
            </div>

            <!-- Items Purchased Table -->
            <% if (order != null && order.getItems() != null && !order.getItems().isEmpty()) { %>
                <div class="products-panel">
                    <div class="products-panel-header">
                        <div>
                            <h3>Purchased Items</h3>
                            <p><%= order.getItems().size() %> line item<%= order.getItems().size() != 1 ? "s" : "" %></p>
                        </div>
                    </div>

                    <div class="products-table-wrapper">
                        <table class="products-table">
                            <thead>
                                <tr>
                                    <th>Item</th>
                                    <th>Unit Price</th>
                                    <th>Qty</th>
                                    <th style="text-align: right;">Total</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (OrderItem item : order.getItems()) {
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
                                                     style="width: 38px; height: 38px;"
                                                     onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                                                <div>
                                                    <strong><%= HtmlUtil.escape(pName) %></strong>
                                                    <% if (p != null && p.getCategory() != null) { %>
                                                        <span style="display: block; font-size: 0.72rem; color: #666;"><%= HtmlUtil.escape(p.getCategory()) %></span>
                                                    <% } %>
                                                </div>
                                            </div>
                                        </td>
                                        <td>₹<%= item.getPrice() %></td>
                                        <td><%= item.getQuantity() %></td>
                                        <td style="text-align: right;"><strong>₹<%= item.getSubtotal() %></strong></td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            <% } %>

            <div style="display: flex; gap: 1rem; justify-content: center; margin-top: 2rem;">
                <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-primary-button" style="padding: 0.95rem 1.8rem;">
                    Continue Shopping
                </a>
                <a href="${pageContext.request.contextPath}/buyer/orders" class="seller-secondary-button" style="padding: 0.95rem 1.8rem;">
                    View My Orders
                </a>
            </div>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/chat-widget.js" defer></script>
</body>
</html>
