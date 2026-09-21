<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard | SameerMart</title>
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
                        <span class="badge badge-blue">System Administrator</span>
                        <span style="font-size: 0.85rem; color: var(--text-muted);">Admin ID: #${user.id}</span>
                    </div>
                    <h1 style="font-size: 2rem; font-weight: 800; color: var(--dark);">Administration Console</h1>
                    <p style="color: var(--text-muted); font-size: 0.95rem;">System metrics, platform integrity, and configuration management for SameerMart.</p>
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
                            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">System Health</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--success);">Operational</div>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon accent">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <ellipse cx="12" cy="5" rx="9" ry="3"></ellipse>
                            <path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"></path>
                            <path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"></path>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Database Storage</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--primary);">H2 File-Backed</div>
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
                        <div class="kpi-label">Security Interceptors</div>
                        <div class="kpi-value" style="font-size: 1.25rem; color: var(--success);">Enforced</div>
                    </div>
                </div>

                <div class="kpi-card">
                    <div class="kpi-icon">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="16 18 22 12 16 6"></polyline>
                            <polyline points="8 6 2 12 8 18"></polyline>
                        </svg>
                    </div>
                    <div>
                        <div class="kpi-label">Framework Stack</div>
                        <div class="kpi-value" style="font-size: 1.25rem;">Boot 3.3.4</div>
                    </div>
                </div>
            </div>

            <!-- System Metrics Card -->
            <div class="card" style="margin-top: 24px;">
                <div class="card-header">
                    <h2 class="card-title" style="font-size: 1.15rem;">Application Architecture Overview</h2>
                </div>
                <div class="card-body">
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 20px;">
                        <div style="border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 18px; background-color: #FAFCFE;">
                            <strong style="color: var(--primary); display: block; margin-bottom: 8px;">Authentication &amp; Sessions</strong>
                            <p style="font-size: 0.88rem; color: var(--text-muted); line-height: 1.6; margin: 0;">
                                Spring Security filter chain with custom <code>SessionAuthenticationInterceptor</code>. Password hashing via BCrypt. Session fixation attacks mitigated on login.
                            </p>
                        </div>
                        <div style="border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 18px; background-color: #FAFCFE;">
                            <strong style="color: var(--primary); display: block; margin-bottom: 8px;">Data Persistence</strong>
                            <p style="font-size: 0.88rem; color: var(--text-muted); line-height: 1.6; margin: 0;">
                                Embedded H2 file-backed storage at <code>./data/sameermart</code> with <code>AUTO_SERVER=TRUE</code> enabled for zero-downtime concurrent connections.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
