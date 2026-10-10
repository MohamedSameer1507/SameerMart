<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
<%@ page import="com.sameer.sameermart.model.Product" %>
<%@ page import="com.sameer.sameermart.dao.ProductDAO" %>
<%@ page import="com.sameer.sameermart.dao.OrderDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.sameer.sameermart.model.TopSellingProduct" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sameer.sameermart.util.HtmlUtil" %>
<%@ page import="com.sameer.sameermart.util.CsrfUtil" %>

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

    CsrfUtil.getOrCreateToken(session);

    String userName = user.getName();
    if (userName == null || userName.trim().isEmpty()) {
        userName = (String) session.getAttribute("userName");
    }
    if (userName == null || userName.trim().isEmpty()) {
        userName = "Seller";
    }

    String success = request.getParameter("success");
    String error = request.getParameter("error");

    ProductDAO productDAO = new ProductDAO();
    List<Product> products = productDAO.findBySellerId(user.getId());

    int totalProducts = products != null ? products.size() : 0;
    int totalStock = 0;
    Set<String> categories = new HashSet<>();

    if (products != null) {
        for (Product p : products) {
            totalStock += p.getQuantity();
            if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
                categories.add(p.getCategory().trim());
            }
        }
    }

    OrderDAO orderDAO = new OrderDAO();
    int totalSellerOrders = orderDAO.getSellerTotalOrders(user.getId());
    BigDecimal totalSellerRevenue = orderDAO.getSellerRevenue(user.getId());
    int totalSellerUnitsSold = orderDAO.getSellerUnitsSold(user.getId());
    Map<String, Integer> ordersByStatus = orderDAO.getSellerOrdersByStatus(user.getId());
    List<TopSellingProduct> topProducts = orderDAO.getTopSellingProducts(user.getId(), 5);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Dashboard - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20260828_3">
</head>
<body>

    <!-- Navigation Bar -->
    <header class="navbar">
        <a href="${pageContext.request.contextPath}/seller/dashboard.jsp" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/seller/dashboard.jsp" class="nav-link active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/seller/add-product.jsp" class="nav-link">Add Product</a></li>
                <li><a href="${pageContext.request.contextPath}/seller/products.jsp" class="nav-link">My Products</a></li>
                <li><a href="${pageContext.request.contextPath}/seller/orders" class="nav-link">Customer Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="seller-page">
        <div class="seller-container">

            <!-- Success / Error Notifications -->
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

            <!-- Seller Header -->
            <section class="page-hero">
                <div class="page-hero-intro">
                    <span class="eyebrow">🏪 SELLER INTELLIGENCE DASHBOARD</span>
                    <h1>Welcome Back, <span class="highlight"><%= HtmlUtil.escape(userName) %></span></h1>
                    <p>Manage your products, monitor inventory stock levels, and grow your multi-vendor marketplace sales.</p>
                </div>
                <div class="seller-role-badge">
                    ROLE: <%= role %>
                </div>
            </section>

            <!-- Stats Overview Cards -->
            <div class="seller-stats-grid">
                <div class="seller-stat-card">
                    <div class="seller-stat-label">Total Products</div>
                    <div class="seller-stat-value"><%= totalProducts %></div>
                    <div class="seller-stat-hint">Active listings in catalog</div>
                </div>

                <div class="seller-stat-card">
                    <div class="seller-stat-label">Total Stock Units</div>
                    <div class="seller-stat-value"><%= totalStock %></div>
                    <div class="seller-stat-hint">Units currently in inventory</div>
                </div>

                <div class="seller-stat-card">
                    <div class="seller-stat-label">Units Sold</div>
                    <div class="seller-stat-value"><%= totalSellerUnitsSold %></div>
                    <div class="seller-stat-hint">Items purchased by buyers</div>
                </div>

                <div class="seller-stat-card">
                    <div class="seller-stat-label">Customer Orders</div>
                    <div class="seller-stat-value"><%= totalSellerOrders %></div>
                    <div class="seller-stat-hint">Orders containing your products</div>
                </div>

                <div class="seller-stat-card">
                    <div class="seller-stat-label">Sales Revenue</div>
                    <div class="seller-stat-value">₹<%= totalSellerRevenue %></div>
                    <div class="seller-stat-hint">Total earnings from sales</div>
                </div>
            </div>

            <!-- Sales Analytics & Visualizations (Option O3) -->
            <section class="seller-section" style="margin-top: 2rem;">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">SALES INTELLIGENCE</span>
                        <h2>Sales Analytics & Performance (Option O3)</h2>
                        <p>Live order lifecycle breakdowns and top-performing merchandise.</p>
                    </div>
                </div>

                <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 1.5rem; margin-bottom: 2rem;">
                    <!-- Order Status Breakdown Chart -->
                    <div class="products-panel" style="padding: 1.8rem;">
                        <h3 style="margin-bottom: 0.5rem; font-size: 1.15rem; font-weight: 800; color: var(--text-main);">Orders by Status</h3>
                        <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.2rem;">Distribution of customer order states</p>
                        <div style="height: 250px; position: relative;">
                            <canvas id="orderStatusChart"></canvas>
                        </div>
                    </div>

                    <!-- Top Selling Products Chart -->
                    <div class="products-panel" style="padding: 1.8rem;">
                        <h3 style="margin-bottom: 0.5rem; font-size: 1.15rem; font-weight: 800; color: var(--text-main);">Top Products by Units Sold</h3>
                        <p style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 1.2rem;">Highest velocity items in catalog</p>
                        <div style="height: 250px; position: relative;">
                            <canvas id="topProductsChart"></canvas>
                        </div>
                    </div>
                </div>

                <!-- Top Products Leaderboard Table -->
                <div class="products-panel" style="margin-bottom: 2rem;">
                    <div class="products-panel-header">
                        <div>
                            <h3>Top-Selling Products Leaderboard</h3>
                            <p>Revenue and quantity sold per product</p>
                        </div>
                    </div>

                    <% if (topProducts == null || topProducts.isEmpty()) { %>
                        <div class="empty-state" style="padding: 2rem;">
                            <p>No product sales recorded yet. Once customer orders are placed, sales rankings appear here.</p>
                        </div>
                    <% } else { %>
                        <div class="products-table-wrapper">
                            <table class="products-table">
                                <thead>
                                    <tr>
                                        <th>Rank</th>
                                        <th>Product Name</th>
                                        <th>Units Sold</th>
                                        <th style="text-align: right;">Total Sales (₹)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% int rank = 1;
                                       for (TopSellingProduct tp : topProducts) { %>
                                        <tr>
                                            <td><strong>#<%= rank++ %></strong></td>
                                            <td><strong><%= HtmlUtil.escape(tp.getProductName()) %></strong></td>
                                            <td><span class="status-badge" style="background: #e6f9ed; color: #1e7e34; border: 1px solid #1e7e34;"><%= tp.getUnitsSold() %> units</span></td>
                                            <td style="text-align: right;"><strong>₹<%= tp.getTotalRevenue() %></strong></td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>
                </div>
            </section>

            <!-- Product Section Header & Quick Add Button -->
            <section class="seller-section">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">INVENTORY & LISTINGS</span>
                        <h2>Manage Products</h2>
                        <p>Create new listings with photos, track inventory levels, or update items.</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/seller/add-product.jsp" class="seller-primary-button">
                        + Add New Product
                    </a>
                </div>

                <!-- Products Table / List View -->
                <div class="products-panel">
                    <div class="products-panel-header">
                        <div>
                            <h3>Your Product Catalog</h3>
                            <p><%= totalProducts > 0 ? "Showing " + totalProducts + " item" + (totalProducts > 1 ? "s" : "") : "No items listed yet" %></p>
                        </div>
                        <% if (totalProducts > 0) { %>
                            <a href="${pageContext.request.contextPath}/seller/products.jsp" class="seller-secondary-button">
                                View Full Inventory
                            </a>
                        <% } %>
                    </div>

                    <% if (products == null || products.isEmpty()) { %>
                        <div class="empty-state">
                            <h3>No Products Added Yet</h3>
                            <p>You haven't listed any items in your seller inventory yet. Start by adding your first product with photo!</p>
                            <a href="${pageContext.request.contextPath}/seller/add-product.jsp" class="seller-primary-button">
                                Add Your First Product
                            </a>
                        </div>
                    <% } else { %>
                        <div class="products-table-wrapper">
                            <table class="products-table">
                                <thead>
                                    <tr>
                                        <th>Photo</th>
                                        <th>ID</th>
                                        <th>Product Name</th>
                                        <th>Category</th>
                                        <th>Price</th>
                                        <th>Stock</th>
                                        <th style="text-align: right;">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        int displayLimit = Math.min(products.size(), 5);
                                        for (int i = 0; i < displayLimit; i++) {
                                            Product p = products.get(i);
                                            String pImg = "default-product.svg";
                                            try {
                                                if (p.getImageUrl() != null && !p.getImageUrl().trim().isEmpty()) {
                                                    pImg = p.getImageUrl().trim();
                                                }
                                            } catch (Throwable t) {
                                                pImg = "default-product.svg";
                                            }
                                            String imgSrc = pImg.startsWith("default-") ? request.getContextPath() + "/images/" + pImg : request.getContextPath() + "/images/products/" + pImg;
                                    %>
                                        <tr>
                                            <td style="width: 50px;">
                                                <img src="<%= imgSrc %>" 
                                                     alt="<%= HtmlUtil.escape(p.getName()) %>" 
                                                     class="product-thumb"
                                                     onerror="this.src='${pageContext.request.contextPath}/images/default-product.svg';">
                                            </td>
                                            <td>#<%= p.getId() %></td>
                                            <td><strong><%= HtmlUtil.escape(p.getName()) %></strong></td>
                                            <td><span class="category-chip"><%= HtmlUtil.escape(p.getCategory()) %></span></td>
                                            <td><strong>₹<%= p.getPrice() %></strong></td>
                                            <td><%= p.getQuantity() %> units</td>
                                            <td style="text-align: right;">
                                                <div class="table-actions" style="justify-content: flex-end;">
                                                    <a href="${pageContext.request.contextPath}/seller/edit-product?id=<%= p.getId() %>" class="action-btn">
                                                        Edit
                                                    </a>
                                                    <form action="${pageContext.request.contextPath}/seller/delete-product" method="post" style="display: inline;" onsubmit="return confirm('Are you sure you want to delete this product?');">
                                                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="id" value="<%= p.getId() %>">
                                                        <input type="hidden" name="redirect" value="/seller/dashboard.jsp">
                                                        <button type="submit" class="action-btn delete-btn">
                                                            Delete
                                                        </button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    <% } %>
                                </tbody>
                            </table>
                        </div>
                    <% } %>
                </div>
            </section>

            <!-- Seller Information & Workflow Feature Cards -->
            <section class="seller-section">
                <div class="section-heading">
                    <div>
                        <span class="eyebrow">SELLER WORKFLOW</span>
                        <h2>Platform Features</h2>
                        <p>Everything you need to sell products effectively on SameerMart.</p>
                    </div>
                </div>

                <div class="seller-feature-grid">
                    <div class="seller-feature-card">
                        <span class="feature-number">01</span>
                        <h3>Photo & Product Listings</h3>
                        <p>Upload high quality product photos, set categories, custom pricing, and inventory quantities.</p>
                    </div>

                    <div class="seller-feature-card">
                        <span class="feature-number">02</span>
                        <h3>Complete CRUD Control</h3>
                        <p>Create, view, update details and photos, or remove discontinued inventory in real-time.</p>
                    </div>

                    <div class="seller-feature-card">
                        <span class="feature-number">03</span>
                        <h3>Marketplace Exposure</h3>
                        <p>Your listings and photos become instantly visible to buyers across SameerMart.</p>
                    </div>
                </div>
            </section>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

    <!-- Chart.js Visualization Scripts (Option O3) -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 1. Order Status Breakdown Chart
            var statusCanvas = document.getElementById('orderStatusChart');
            if (statusCanvas && typeof Chart !== 'undefined') {
                new Chart(statusCanvas, {
                    type: 'doughnut',
                    data: {
                        labels: ['Pending', 'Confirmed', 'Shipped', 'Delivered', 'Cancelled'],
                        datasets: [{
                            data: [
                                <%= ordersByStatus != null ? ordersByStatus.getOrDefault("PENDING", 0) : 0 %>,
                                <%= ordersByStatus != null ? ordersByStatus.getOrDefault("CONFIRMED", 0) : 0 %>,
                                <%= ordersByStatus != null ? ordersByStatus.getOrDefault("SHIPPED", 0) : 0 %>,
                                <%= ordersByStatus != null ? ordersByStatus.getOrDefault("DELIVERED", 0) : 0 %>,
                                <%= ordersByStatus != null ? ordersByStatus.getOrDefault("CANCELLED", 0) : 0 %>
                            ],
                            backgroundColor: ['#f39c12', '#3498db', '#9b59b6', '#2ecc71', '#e74c3c'],
                            borderWidth: 2,
                            borderColor: '#000'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: {
                                    font: { family: 'Courier New, monospace', weight: 'bold', size: 11 },
                                    color: '#000'
                                }
                            }
                        }
                    }
                });
            }

            // 2. Top Products Bar Chart
            var topCanvas = document.getElementById('topProductsChart');
            if (topCanvas && typeof Chart !== 'undefined') {
                new Chart(topCanvas, {
                    type: 'bar',
                    data: {
                        labels: [
                            <% if (topProducts != null) {
                                for (int i = 0; i < topProducts.size(); i++) { %>
                                '<%= HtmlUtil.escape(topProducts.get(i).getProductName()).replace("'", "\\'") %>'<%= (i < topProducts.size() - 1) ? "," : "" %>
                            <% } } %>
                        ],
                        datasets: [{
                            label: 'Units Sold',
                            data: [
                                <% if (topProducts != null) {
                                    for (int i = 0; i < topProducts.size(); i++) { %>
                                    <%= topProducts.get(i).getUnitsSold() %><%= (i < topProducts.size() - 1) ? "," : "" %>
                                <% } } %>
                            ],
                            backgroundColor: '#000',
                            borderColor: '#000',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: { precision: 0, font: { family: 'Courier New, monospace', weight: 'bold' } }
                            },
                            x: {
                                ticks: { font: { family: 'Courier New, monospace', weight: 'bold' } }
                            }
                        },
                        plugins: {
                            legend: { display: false }
                        }
                    }
                });
            }
        });
    </script>

</body>
</html>