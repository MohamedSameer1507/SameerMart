<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - SameerMart</title>
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
        <div class="auth-card">
            <div class="auth-header">
                <span class="eyebrow">🚀 JOIN SAMEERMART FREE</span>
                <h2>Create your <span class="highlight">Account</span></h2>
                <p class="subtitle">Join as a verified Buyer or register to sell your products</p>
            </div>

            <%
                String error = request.getParameter("error");
                if (error != null) {
                    String safeError = HtmlUtil.escape(error);
            %>
                <div class="message error"><%= safeError %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/RegisterServlet" method="post">

                <div class="role-selector">
                    <div class="role-option">
                        <input type="radio" id="role-buyer" name="role" value="BUYER" checked>
                        <label for="role-buyer">🛍️ Buyer</label>
                    </div>
                    <div class="role-option">
                        <input type="radio" id="role-seller" name="role" value="SELLER">
                        <label for="role-seller">🏪 Seller</label>
                    </div>
                </div>

                <div class="form-group">
                    <label for="name">Full Name</label>
                    <input type="text" id="name" name="name" placeholder="e.g. Sameer Ahmed" required>
                </div>

                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" placeholder="e.g. name@example.com" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="create a secure password" required>
                </div>

                <button type="submit" class="primary-button">Create Account Free →</button>
            </form>

            <div class="auth-footer">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Sign in here</a></p>
            </div>
        </div>
    </main>

    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/chat-widget.js" defer></script>
</body>
</html>