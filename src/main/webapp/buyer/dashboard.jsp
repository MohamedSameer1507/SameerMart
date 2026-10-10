<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.model.User" %>
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

    String userName = user.getName();
    if (userName == null || userName.trim().isEmpty()) {
        userName = (String) session.getAttribute("userName");
    }
    if (userName == null || userName.trim().isEmpty()) {
        userName = "Buyer";
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Buyer Dashboard - SameerMart</title>
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
                <li><a href="${pageContext.request.contextPath}/buyer/dashboard.jsp" class="nav-link active">Dashboard</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/marketplace" class="nav-link">Marketplace</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/cart" class="nav-link">My Cart</a></li>
                <li><a href="${pageContext.request.contextPath}/buyer/orders" class="nav-link">My Orders</a></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="nav-link">Logout</a></li>
            </ul>
        </nav>
    </header>

    <!-- Main Content -->
    <main class="seller-page">
        <div class="seller-container">

            <section class="page-hero">
                <div class="page-hero-intro">
                    <span class="eyebrow">🛍️ BUYER ACCOUNT HUB</span>
                    <h1>Welcome, <span class="highlight"><%= HtmlUtil.escape(userName) %></span></h1>
                    <p>Explore multi-vendor listings, connect with verified sellers, track deliveries, and manage your orders.</p>
                </div>
                <div class="seller-role-badge">
                    ROLE: <%= role %>
                </div>
            </section>

            <section class="feature-card" style="display: flex; justify-content: space-between; align-items: center; gap: 1.5rem; flex-wrap: wrap; margin-bottom: 2rem;">
                <div>
                    <span class="eyebrow">🏪 LIVE CATALOG</span>
                    <h2 style="font-size: 1.4rem; font-weight: 800; color: var(--text-main); margin: 0.3rem 0;">Browse Marketplace Products</h2>
                    <p style="color: var(--text-muted); font-size: 0.95rem;">Explore items listed by sellers, add to your cart with real-time stock verification, and place orders directly.</p>
                </div>
                <div style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap;">
                    <a href="${pageContext.request.contextPath}/buyer/marketplace" class="seller-primary-button">
                        Browse Marketplace →
                    </a>
                </div>
            </section>

        </div>
    </main>

    <!-- Footer -->
    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/chat-widget.js" defer></script>
</body>
</html>