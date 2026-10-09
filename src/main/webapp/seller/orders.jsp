<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.SellerOrderItem" %>
<%@ page import="com.sameer.sameermart.dao.OrderDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.sameer.sameermart.util.HtmlUtil" %>

<%
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please+login+first");
        return;
    }

    String role = user.getRole();
    if (role == null || !"SELLER".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Seller+access+required");
        return;
    }

    List<SellerOrderItem> sellerOrders = (List<SellerOrderItem>) request.getAttribute("sellerOrders");
    BigDecimal totalRevenue = (BigDecimal) request.getAttribute("totalRevenue");
    Integer totalOrders = (Integer) request.getAttribute("totalOrders");

    if (sellerOrders == null || totalRevenue == null || totalOrders == null) {
        OrderDAO orderDAO = new OrderDAO();
        sellerOrders = orderDAO.findSellerOrders(user.getId());
        totalRevenue = orderDAO.getSellerRevenue(user.getId());
        totalOrders = orderDAO.getSellerTotalOrders(user.getId());
    }

    int totalItemsSold = 0;
    if (sellerOrders != null) {
        for (SellerOrderItem item : sellerOrders) {
            totalItemsSold += item.getQuantity();
        }
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer Orders - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20260828_3">
</head>
<body>

    <!-- Navigation Bar -->
    <header class="navbar">
        <a href="${pageContext.request.contextPath}/seller/dashboard.jsp" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/seller/dashboard.jsp" class="nav-link">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/seller/add-product.jsp" class="nav-link">Add Product</a></li>
                <li><a href="${pageContext.request.contextPath}/seller/products.jsp" class="nav-link">My Products</a></li>
                <li><a href="${pageContext.request.contextPath}/seller/orders" class="nav-link active">Customer Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="seller-page">
        <div class="seller-container">

            <% if (success != null && !success.trim().isEmpty()) { %>
                <div class="message success" style="margin-bottom: 1.5rem;"><%= HtmlUtil.escape(success) %></div>
            <% } %>

            <% if (error != null && !error.trim().isEmpty()) { %>
                <div class="message error" style="margin-bottom: 1.5rem;"><%= HtmlUtil.escape(error) %></div>
            <% } %>

            <section class="seller-header">
                <div class="seller-introduction">
                    <span class="eyebrow">SELLER PANEL</span>
                    <h1>Customer Orders</h1>
                    <p>Review orders placed by buyers for your listed products and monitor fulfilled sales.</p>
                </div>
                <a href="${pageContext.request.contextPath}/seller/products.jsp" class="seller-secondary-button">
                    View Inventory
                </a>
            </section>

            <!-- Stats Overview Cards -->
            <div class="seller-stats-grid">
                <div class="seller-stat-card">
                    <div class="seller-stat-label">Orders Count</div>
                    <div class="seller-stat-value"><%= totalOrders %></div>
                    <div class="seller-stat-hint">Customer orders containing your products</div>
                </div>

                <div class="seller-stat-card">
                    <div class="seller-stat-label">Units Sold</div>
                    <div class="seller-stat-value"><%= totalItemsSold %></div>
                    <div class="seller-stat-hint">Total quantity purchased by buyers</div>
                </div>

                <div class="seller-stat-card">
                    <div class="seller-stat-label">Total Sales Revenue</div>
                    <div class="seller-stat-value">₹<%= totalRevenue %></div>
                    <div class="seller-stat-hint">Earned from purchased items</div>
                </div>
            </div>

            <!-- Orders Table Section -->
            <div class="products-panel">
                <div class="products-panel-header">
                    <div>
                        <h3>Product Sales Records</h3>
                        <p><%= sellerOrders != null ? sellerOrders.size() : 0 %> line item<%= (sellerOrders != null && sellerOrders.size() == 1) ? "" : "s" %> ordered</p>
                    </div>
                </div>

                <% if (sellerOrders == null || sellerOrders.isEmpty()) { %>
                    <div class="empty-state">
                        <h3>No Customer Orders Yet</h3>
                        <p>No buyers have placed orders for your products yet. Keep your inventory stocked and up to date!</p>
                        <a href="${pageContext.request.contextPath}/seller/products.jsp" class="seller-primary-button">
                            Manage My Products
                        </a>
                    </div>
                <% } else { %>
                    <div class="products-table-wrapper">
                        <table class="products-table">
                            <thead>
                                <tr>
                                    <th>Order #</th>
                                    <th>Date</th>
                                    <th>Buyer Details</th>
                                    <th>Product</th>
                                    <th>Qty</th>
                                    <th>Unit Price</th>
                                    <th>Subtotal</th>
                                    <th>Status</th>
                                    <th style="text-align: right;">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (SellerOrderItem item : sellerOrders) {
                                    String pImg = "default-product.svg";
                                    try {
                                        if (item.getProductImage() != null && !item.getProductImage().trim().isEmpty()) {
                                            pImg = item.getProductImage().trim();
                                        }
                                    } catch (Throwable t) {
                                        pImg = "default-product.svg";
                                    }
                                    String imgSrc = pImg.startsWith("default-") ? request.getContextPath() + "/images/" + pImg : request.getContextPath() + "/images/products/" + pImg;
                                    String itemStatus = item.getOrderStatus() != null ? item.getOrderStatus().trim().toUpperCase() : "CONFIRMED";
                                    String statusClass = "status-" + itemStatus.toLowerCase();
                                %>
                                    <tr>
                                        <td><strong>#<%= item.getOrderId() %></strong></td>
                                        <td><%= item.getOrderDate() != null ? sdf.format(item.getOrderDate()) : "Recently" %></td>
                                        <td>
                                            <strong><%= HtmlUtil.escape(item.getBuyerName()) %></strong>
                                            <span style="display: block; font-size: 0.75rem; color: #666;"><%= HtmlUtil.escape(item.getBuyerEmail()) %></span>
                                        </td>
                                        <td>
                                            <div style="display: flex; align-items: center; gap: 10px;">
                                                <img src="<%= imgSrc %>" 
                                                     alt="<%= HtmlUtil.escape(item.getProductName()) %>" 
                                                     class="product-thumb"
                                                     onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                                                <div>
                                                    <strong><%= HtmlUtil.escape(item.getProductName()) %></strong>
                                                    <% if (item.getProductCategory() != null) { %>
                                                        <span class="category-chip" style="font-size: 0.7rem; padding: 1px 6px;"><%= HtmlUtil.escape(item.getProductCategory()) %></span>
                                                    <% } %>
                                                </div>
                                            </div>
                                        </td>
                                        <td><%= item.getQuantity() %></td>
                                        <td>₹<%= item.getPrice() %></td>
                                        <td><strong>₹<%= item.getSubtotal() %></strong></td>
                                        <td>
                                            <span class="status-badge <%= statusClass %>">
                                                <%= HtmlUtil.escape(itemStatus) %>
                                            </span>
                                        </td>
                                        <td style="text-align: right;">
                                            <div class="order-actions-cell">
                                                <% if ("CONFIRMED".equals(itemStatus)) { %>
                                                    <form action="${pageContext.request.contextPath}/seller/order-status" method="POST" style="margin: 0; display: inline;">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="orderId" value="<%= item.getOrderId() %>">
                                                        <input type="hidden" name="status" value="SHIPPED">
                                                        <button type="submit" class="btn-status-action btn-status-ship">Mark Shipped</button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/seller/order-status" method="POST" style="margin: 0; display: inline;" onsubmit="return confirm('Cancel order #<%= item.getOrderId() %>?');">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="orderId" value="<%= item.getOrderId() %>">
                                                        <input type="hidden" name="status" value="CANCELLED">
                                                        <button type="submit" class="btn-status-action btn-status-cancel">Cancel</button>
                                                    </form>
                                                <% } else if ("SHIPPED".equals(itemStatus)) { %>
                                                    <form action="${pageContext.request.contextPath}/seller/order-status" method="POST" style="margin: 0; display: inline;">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="orderId" value="<%= item.getOrderId() %>">
                                                        <input type="hidden" name="status" value="DELIVERED">
                                                        <button type="submit" class="btn-status-action btn-status-deliver">Mark Delivered</button>
                                                    </form>
                                                    <form action="${pageContext.request.contextPath}/seller/order-status" method="POST" style="margin: 0; display: inline;" onsubmit="return confirm('Cancel order #<%= item.getOrderId() %>?');">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="orderId" value="<%= item.getOrderId() %>">
                                                        <input type="hidden" name="status" value="CANCELLED">
                                                        <button type="submit" class="btn-status-action btn-status-cancel">Cancel</button>
                                                    </form>
                                                <% } else if ("DELIVERED".equals(itemStatus)) { %>
                                                    <span style="font-size: 0.75rem; color: #166534; font-weight: 700;">✓ Fulfilled</span>
                                                <% } else if ("CANCELLED".equals(itemStatus)) { %>
                                                    <span style="font-size: 0.75rem; color: #991b1b; font-weight: 700;">Cancelled</span>
                                                <% } else { %>
                                                    <span style="font-size: 0.75rem; color: #666;">-</span>
                                                <% } %>
                                            </div>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                <% } %>
            </div>

            <div class="account-link" style="text-align: left; margin-top: 1.5rem;">
                <a href="${pageContext.request.contextPath}/seller/dashboard.jsp">← Back to Dashboard</a>
            </div>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2026 SameerMart. All rights reserved.</p>
    </footer>

</body>
</html>
