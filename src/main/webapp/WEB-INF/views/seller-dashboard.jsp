<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Dashboard | SameerMart</title>
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
                        <span class="badge badge-red">Seller Portal</span>
                        <span style="font-size: 0.85rem; color: var(--text-muted);">Merchant ID: #${user.id}</span>
                    </div>
                    <h1 style="font-size: 2rem; font-weight: 800; color: var(--dark);">Welcome, <c:out value="${user.name}"/>!</h1>
                    <p style="color: var(--text-muted); font-size: 0.95rem;">Seller hub for inventory management and storefront activity on SameerMart.</p>
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
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                            <polyline points="9 22 9 12 15 12 15 22"></polyline>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Store Status</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--success);">Active</div>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon accent">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="12" cy="12" r="10"></circle>
                            <polyline points="12 6 12 12 16 14"></polyline>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Merchant Tier</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--accent);">Tier 1 Partner</div>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Security &amp; Auth</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--primary);">Verified</div>
                    </div>
                </div>
            </div>

            <!-- Merchant Card -->
            <div class="card" style="margin-top: 24px;">
                <div class="card-header">
                    <h2 class="card-title" style="font-size: 1.15rem;">Seller Storefront Notice</h2>
                </div>
                <div class="card-body">
                    <p style="color: var(--text); line-height: 1.7; margin-bottom: 16px;">
                        Welcome to your SameerMart Seller Console. Your account has verified merchant privileges. In this demo environment, product catalog inventory is pre-seeded with sample stock in the persistent database.
                    </p>
                    <div style="background-color: #F8FAFC; border: 1px solid var(--border); border-radius: var(--radius); padding: 18px;">
                        <strong style="color: var(--primary); display: block; margin-bottom: 6px;">Storefront Guidelines:</strong>
                        <ul style="color: var(--text-muted); font-size: 0.9rem; padding-left: 20px; line-height: 1.6;">
                            <li>All items sold on SameerMart must adhere to quality benchmarks and accurate stock counts.</li>
                            <li>Payment settlements are calculated in INR with simulated fast fulfillment.</li>
                            <li>Buyer transactions are guarded by session authentication and CSRF tokens.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
