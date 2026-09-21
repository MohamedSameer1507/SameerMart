<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmed | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp">
        <jsp:param name="activeNav" value="cart" />
    </jsp:include>

    <main class="main-content">
        <div class="container">
            <div class="success-screen">
                <div class="success-icon-wrap">
                    <svg width="42" height="42" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.8" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="20 6 9 17 4 12"></polyline>
                    </svg>
                </div>

                <h1 style="font-size: 2rem; font-weight: 800; color: var(--dark); margin-bottom: 8px;">Order Confirmed!</h1>
                <p style="color: var(--text-muted); font-size: 1rem; max-width: 440px; margin: 0 auto 16px auto;">
                    Your mock transaction has been successfully processed. We've queued your items for simulated delivery.
                </p>

                <div>
                    <span class="order-pill">Order Reference: #${orderId}</span>
                </div>

                <div style="background-color: #F8FAFC; border: 1px solid var(--border); border-radius: var(--radius); padding: 16px; margin: 24px 0; text-align: left; font-size: 0.88rem; color: var(--text-muted);">
                    <div style="display: flex; gap: 8px; margin-bottom: 6px;">
                        <span style="color: var(--primary); font-weight: 600;">Status:</span>
                        <span>Payment Completed &bull; Ready for Fulfillment</span>
                    </div>
                    <div style="display: flex; gap: 8px;">
                        <span style="color: var(--primary); font-weight: 600;">Shipping:</span>
                        <span>Standard Simulated Express (24-48 Hours)</span>
                    </div>
                </div>

                <div style="display: flex; gap: 14px; justify-content: center; flex-wrap: wrap;">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-accent btn-lg">
                        Continue Shopping
                    </a>
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-outline-primary btn-lg">
                        Go to Dashboard
                    </a>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
