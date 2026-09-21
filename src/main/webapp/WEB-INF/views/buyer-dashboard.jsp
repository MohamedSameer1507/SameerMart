<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Buyer Dashboard | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp">
        <jsp:param name="activeNav" value="dashboard" />
    </jsp:include>

    <main class="main-content">
        <div class="container">
            <!-- Header Banner -->
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 32px; flex-wrap: wrap; gap: 16px;">
                <div>
                    <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 6px;">
                        <span class="badge badge-blue">Buyer Account</span>
                        <span style="font-size: 0.85rem; color: var(--text-muted);">ID: #${user.id}</span>
                    </div>
                    <h1 style="font-size: 2rem; font-weight: 800; color: var(--dark);">Welcome back, <c:out value="${user.name}"/>!</h1>
                    <p style="color: var(--text-muted); font-size: 0.95rem;">Manage your shopping, view items in your cart, and browse products.</p>
                </div>
                <div>
                    <form method="post" action="${pageContext.request.contextPath}/login/logout" style="margin:0;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <button type="submit" class="btn btn-outline-danger">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                <polyline points="16 17 21 12 16 7"></polyline>
                                <line x1="21" y1="12" x2="9" y2="12"></line>
                            </svg>
                            Sign out
                        </button>
                    </form>
                </div>
            </div>

            <!-- KPI Metric Grid -->
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-icon">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Account Tier</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--primary);">Verified Buyer</div>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon accent">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="9" cy="21" r="1"></circle>
                            <circle cx="20" cy="21" r="1"></circle>
                            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Shopping Cart</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--accent);">Active &amp; Ready</div>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Security Shield</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--success);">Protected</div>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="12" y1="1" x2="12" y2="23"></line>
                            <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Store Currency</div>
                        <div class="kpi-value" style="font-size: 1.25rem;">INR (&#8377;)</div>
                    </div>
                </div>
            </div>

            <!-- Quick Action Cards Grid -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 24px; margin-top: 12px;">
                <div class="card">
                    <div class="card-body" style="padding: 32px;">
                        <div style="width: 48px; height: 48px; background-color: #EBF3FA; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: var(--primary); margin-bottom: 18px;">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="3" y="3" width="7" height="7"></rect>
                                <rect x="14" y="3" width="7" height="7"></rect>
                                <rect x="14" y="14" width="7" height="7"></rect>
                                <rect x="3" y="14" width="7" height="7"></rect>
                            </svg>
                        </div>
                        <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--dark); margin-bottom: 8px;">Explore Products</h3>
                        <p style="color: var(--text-muted); font-size: 0.92rem; margin-bottom: 24px; line-height: 1.6;">
                            Browse through our full catalog of electronics, lifestyle, clothing, and accessories with instant search and filtering.
                        </p>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-block">
                            Browse Catalog
                        </a>
                    </div>
                </div>

                <div class="card">
                    <div class="card-body" style="padding: 32px;">
                        <div style="width: 48px; height: 48px; background-color: #FEE2E2; border-radius: 10px; display: flex; align-items: center; justify-content: center; color: var(--accent); margin-bottom: 18px;">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
                                <line x1="3" y1="6" x2="21" y2="6"></line>
                                <path d="M16 10a4 4 0 0 1-8 0"></path>
                            </svg>
                        </div>
                        <h3 style="font-size: 1.25rem; font-weight: 700; color: var(--dark); margin-bottom: 8px;">Shopping Cart</h3>
                        <p style="color: var(--text-muted); font-size: 0.92rem; margin-bottom: 24px; line-height: 1.6;">
                            Review your selected cart items, update desired quantities, and proceed through our seamless mock checkout flow.
                        </p>
                        <a href="${pageContext.request.contextPath}/cart" class="btn btn-accent btn-block">
                            Go to Cart
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
