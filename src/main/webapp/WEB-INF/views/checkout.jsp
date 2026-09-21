<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout | SameerMart</title>
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
                <div class="step-item active">
                    <div class="step-number">2</div>
                    <div class="step-label">Order Review</div>
                </div>
                <div class="step-item">
                    <div class="step-number">3</div>
                    <div class="step-label">Payment</div>
                </div>
            </div>

            <div class="two-column-layout">
                <!-- Left: Order Review Details -->
                <div>
                    <div class="card" style="margin-bottom: 24px;">
                        <div class="card-header">
                            <h2 class="card-title" style="font-size: 1.15rem;">Review Order Items</h2>
                        </div>
                        <div class="card-body">
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <c:forEach items="${cart.items}" var="item">
                                    <li style="display: flex; justify-content: space-between; align-items: center; padding: 14px 0; border-bottom: 1px solid var(--border);">
                                        <div>
                                            <strong style="color: var(--dark); font-size: 1rem;"><c:out value="${item.name}"/></strong>
                                            <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 4px;">
                                                Quantity: ${item.quantity} &times; &#8377;<fmt:formatNumber value="${item.unitPriceMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/>
                                            </div>
                                        </div>
                                        <div style="font-weight: 700; font-size: 1.1rem; color: var(--primary);">
                                            &#8377;<fmt:formatNumber value="${item.lineTotalMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header">
                            <h2 class="card-title" style="font-size: 1.15rem;">Delivery Destination</h2>
                        </div>
                        <div class="card-body" style="display: flex; gap: 16px;">
                            <div style="color: var(--primary);">
                                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                                    <circle cx="12" cy="10" r="3"></circle>
                                </svg>
                            </div>
                            <div>
                                <strong style="color: var(--dark); display: block; margin-bottom: 4px;">Standard Doorstep Delivery</strong>
                                <p style="font-size: 0.9rem; color: var(--text-muted); line-height: 1.5; margin: 0;">
                                    Express Simulated Delivery &bull; Free of Charge &bull; Handled via SameerMart Logistics
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Right: Final Bill and CTA -->
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-title" style="font-size: 1.15rem;">Payment Summary</h2>
                    </div>
                    <div class="card-body">
                        <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 0.95rem;">
                            <span style="color: var(--text-muted);">Items Total:</span>
                            <span style="font-weight: 600;">&#8377;<fmt:formatNumber value="${cart.totalMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/></span>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 0.95rem;">
                            <span style="color: var(--text-muted);">Delivery Fee:</span>
                            <span style="font-weight: 600; color: var(--success);">FREE</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; align-items: baseline; padding-top: 16px; border-top: 2px dashed var(--border); margin-bottom: 24px;">
                            <span style="font-size: 1.1rem; font-weight: 700; color: var(--dark);">Payable Total:</span>
                            <span style="font-size: 1.6rem; font-weight: 800; color: var(--primary);">&#8377;<fmt:formatNumber value="${cart.totalMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/></span>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/checkout/payment">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <button type="submit" class="btn btn-accent btn-lg btn-block">
                                Continue to Mock Payment
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="5" y1="12" x2="19" y2="12"></line>
                                    <polyline points="12 5 19 12 12 19"></polyline>
                                </svg>
                            </button>
                        </form>

                        <div style="text-align: center; margin-top: 16px;">
                            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline btn-sm" style="width: 100%;">
                                Return to Cart
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
