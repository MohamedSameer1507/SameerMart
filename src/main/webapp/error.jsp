<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - SameerMart</title>
    <link rel="icon" href="data:,">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=20261010_2">
</head>
<body>

    <header class="navbar">
        <a href="${pageContext.request.contextPath}/" class="brand">SameerMart</a>
        <nav>
            <ul class="nav-links">
                <li><a href="${pageContext.request.contextPath}/" class="nav-link">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/login.jsp" class="nav-link">Login</a></li>
            </ul>
        </nav>
    </header>

    <main class="page">
        <div class="auth-card" style="max-width: 520px; text-align: center;">
            <div class="auth-header">
                <span class="eyebrow" style="background: var(--danger-bg); color: var(--danger-text); border-color: var(--danger-border);">⚠️ REQUEST NOTICE</span>
                <h2>Oops! Something <span class="highlight">Went Wrong</span></h2>
            </div>

            <p style="color: var(--text-muted); margin: 1rem 0 2rem; line-height: 1.6; font-size: 0.95rem;">
                The request could not be completed as expected. An error was encountered while processing your request. Please return to the homepage or try again shortly.
            </p>

            <div style="display: flex; gap: 1rem; justify-content: center;">
                <a href="${pageContext.request.contextPath}/" class="btn-link">
                    <button type="button" class="primary-button">Go to Homepage →</button>
                </a>
                <a href="javascript:history.back()" class="btn-link">
                    <span class="secondary-button">Go Back</span>
                </a>
            </div>
        </div>
    </main>

    <footer class="footer">
        <p>© 2026 SameerMart. Designed with modern Java 17 & Servlets for Anna University Capstone.</p>
    </footer>

</body>
</html>
