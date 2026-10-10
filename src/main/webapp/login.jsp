<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.sameer.sameermart.util.HtmlUtil" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261010_2">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/chat-widget.css?v=20261010_2">
</head>
<body>

    <header class="navbar">
        <a href="${pageContext.request.contextPath}/" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/login.jsp" class="nav-link active">Login</a></li>
                <li><a href="${pageContext.request.contextPath}/register.jsp" class="nav-link">Register</a></li>
            </ul>
        </nav>
    </header>

    <main class="page">
        <div class="auth-card">
            <div class="auth-header">
                <span class="eyebrow">🔑 SECURE ACCOUNT ACCESS</span>
                <h2>Sign in to <span class="highlight">SameerMart</span></h2>
                <p class="subtitle">Access your multi-vendor dashboard, orders, and listings</p>
            </div>

            <%
                String error = request.getParameter("error");
                String success = request.getParameter("success");
                if (error != null) {
                    String safeError = HtmlUtil.escape(error);
            %>
                <div class="message error"><%= safeError %></div>
            <% } %>

            <%
                if (success != null) {
                    String safeSuccess = HtmlUtil.escape(success);
            %>
                <div class="message success"><%= safeSuccess %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="email">Email Address</label>
                    <input type="email" id="email" name="email" placeholder="e.g. name@example.com" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="enter your password" required>
                </div>

                <button type="submit" class="primary-button">Sign In to Account →</button>
            </form>

            <div class="auth-footer">
                <p>Don't have an account yet? <a href="${pageContext.request.contextPath}/register.jsp">Create one free</a></p>
            </div>
        </div>
    </main>

    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

    <script src="${pageContext.request.contextPath}/js/chat-widget.js" defer></script>
</body>
</html>