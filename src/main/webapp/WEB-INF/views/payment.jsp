<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mock Payment | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp">
        <jsp:param name="activeNav" value="cart" />
    </jsp:include>

    <main class="main-content">
        <div class="container">
            <!-- Progress Steps Indicator -->
            <div class="steps-progress" style="max-width: 600px; margin: 0 auto 36px auto;">
                <div class="step-item completed">
                    <div class="step-number">✓</div>
                    <div class="step-label">Cart</div>
                </div>
                <div class="step-item completed">
                    <div class="step-number">✓</div>
                    <div class="step-label">Order Review</div>
                </div>
                <div class="step-item active">
                    <div class="step-number">3</div>
                    <div class="step-label">Payment</div>
                </div>
            </div>

            <div style="max-width: 520px; margin: 0 auto;">
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title" style="font-size: 1.2rem;">Complete Mock Payment</h2>
                        <span class="badge badge-blue">Sandbox Mode</span>
                    </div>

                    <div class="card-body">
                        <!-- Simulated Payment Card Visual -->
                        <div class="mock-payment-card">
                            <div class="mock-card-chip"></div>
                            <div class="mock-card-number">4242 &bull;&bull;&bull;&bull; &bull;&bull;&bull;&bull; 4242</div>
                            <div class="mock-card-details">
                                <div>
                                    <div style="font-size: 0.7rem; opacity: 0.7;">Card Holder</div>
                                    <div style="font-weight: 700; font-size: 0.95rem;">SameerMart Buyer</div>
                                </div>
                                <div>
                                    <div style="font-size: 0.7rem; opacity: 0.7;">Expires</div>
                                    <div style="font-weight: 700; font-size: 0.95rem;">12 / 28</div>
                                </div>
                            </div>
                        </div>

                        <!-- Sandbox Alert -->
                        <div class="alert alert-info">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="flex-shrink:0;">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            <span>This is a demo payment simulator. No real money or financial information is processed.</span>
                        </div>

                        <!-- Amount Display -->
                        <div style="text-align: center; padding: 18px 0; margin-bottom: 20px;">
                            <div style="font-size: 0.9rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 4px;">Total Authorized Amount</div>
                            <div style="font-size: 2.2rem; font-weight: 800; color: var(--primary);">
                                &#8377;<fmt:formatNumber value="${cart.totalMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/>
                            </div>
                        </div>

                        <!-- Confirmation Form -->
                        <form method="post" action="${pageContext.request.contextPath}/checkout/confirm">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <button type="submit" class="btn btn-accent btn-lg btn-block">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                    <polyline points="20 6 9 17 4 12"></polyline>
                                </svg>
                                Confirm Mock Payment
                            </button>
                        </form>

                        <div style="text-align: center; margin-top: 16px;">
                            <a href="${pageContext.request.contextPath}/cart" style="font-size: 0.9rem; color: var(--text-muted);">
                                Cancel transaction and return to Cart
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
