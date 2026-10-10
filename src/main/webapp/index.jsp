<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SameerMart - Modern Multi-Vendor Marketplace</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261010_2">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat-widget.css?v=20261010_2">
</head>
<body>

    <header class="navbar">
        <a href="${pageContext.request.contextPath}/" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/login.jsp" class="nav-link">Login</a></li>
                <li><a href="${pageContext.request.contextPath}/register.jsp" class="nav-link active">Register</a></li>
            </ul>
        </nav>
    </header>

    <main class="page">
        <div class="hero">
            <span class="eyebrow">✨ NEXT-GEN MULTI-VENDOR PLATFORM</span>
            <h1>Buy & Sell Direct with <span class="highlight">Confidence</span></h1>
            <p>Experience seamless multi-vendor e-commerce with real-time stock tracking, atomic transactional checkout, interactive seller analytics, and Google Gemini AI support.</p>

            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/register.jsp" class="btn-link">
                    <button type="button" class="primary-button">Get Started Free →</button>
                </a>
                <a href="${pageContext.request.contextPath}/login.jsp" class="btn-link">
                    <span class="secondary-button">Sign In</span>
                </a>
            </div>

            <div class="features-grid">
                <div class="feature-card">
                    <h3>🛍️ For Buyers</h3>
                    <p>Explore verified seller listings, manage wishlists, enjoy atomic cart checkouts, and receive order delivery updates.</p>
                </div>
                <div class="feature-card">
                    <h3>🏪 For Sellers</h3>
                    <p>Publish products with instant image uploads, track fulfillment workflows, and analyze real-time sales intelligence with Chart.js.</p>
                </div>
                <div class="feature-card">
                    <h3>🤖 AI Powered</h3>
                    <p>Instant answers on shipping, returns, order status, and catalog recommendations via our 24/7 intelligent assistant.</p>
                </div>
            </div>
        </div>
    </main>

    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/chat-widget.js" defer></script>
</body>
</html>