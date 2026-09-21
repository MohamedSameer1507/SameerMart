<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="navbar">
    <div class="container navbar-inner">
        <a href="${pageContext.request.contextPath}/products" class="navbar-brand" title="SameerMart Home">
            <span class="brand-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="9" cy="21" r="1"></circle>
                    <circle cx="20" cy="21" r="1"></circle>
                    <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                </svg>
            </span>
            <span>Sameer<span class="brand-accent">Mart</span></span>
        </a>

        <nav>
            <ul class="navbar-nav" id="navbarMenu">
                <li>
                    <a href="${pageContext.request.contextPath}/products" class="nav-link ${param.activeNav == 'products' ? 'active' : ''}">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="3" width="7" height="7"></rect>
                            <rect x="14" y="3" width="7" height="7"></rect>
                            <rect x="14" y="14" width="7" height="7"></rect>
                            <rect x="3" y="14" width="7" height="7"></rect>
                        </svg>
                        Products
                    </a>
                </li>
                <li>
                    <a href="${pageContext.request.contextPath}/cart" class="nav-link ${param.activeNav == 'cart' ? 'active' : ''}">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
                            <line x1="3" y1="6" x2="21" y2="6"></line>
                            <path d="M16 10a4 4 0 0 1-8 0"></path>
                        </svg>
                        Cart
                    </a>
                </li>
                <c:if test="${not empty sessionScope.authenticatedUser}">
                    <li>
                        <a href="${pageContext.request.contextPath}/dashboard" class="nav-link ${param.activeNav == 'dashboard' ? 'active' : ''}">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                <line x1="3" y1="9" x2="21" y2="9"></line>
                                <line x1="9" y1="21" x2="9" y2="9"></line>
                            </svg>
                            Dashboard
                        </a>
                    </li>
                </c:if>
            </ul>
        </nav>

        <div class="navbar-actions">
            <c:choose>
                <c:when test="${not empty sessionScope.authenticatedUser}">
                    <div style="display:flex; align-items:center; gap:10px;">
                        <span class="badge ${sessionScope.authenticatedUser.role eq 'BUYER' ? 'badge-blue' : 'badge-red'}">
                            <c:out value="${sessionScope.authenticatedUser.role}"/>
                        </span>
                        <span style="font-weight: 600; font-size: 0.9rem; color: var(--dark); display: none; @media(min-width: 600px){display:inline;}">
                            <c:out value="${sessionScope.authenticatedUser.name}"/>
                        </span>
                        <form method="post" action="${pageContext.request.contextPath}/login/logout" style="display:inline; margin:0;">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <button type="submit" class="btn btn-outline-danger btn-sm" title="Sign out from SameerMart">
                                Sign out
                            </button>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary btn-sm">Sign in</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn btn-accent btn-sm">Create Account</a>
                </c:otherwise>
            </c:choose>

            <button class="mobile-toggle" id="mobileMenuBtn" aria-label="Toggle navigation menu" onclick="var m = document.getElementById('navbarMenu'); m.style.display = m.style.display === 'flex' ? 'none' : 'flex';">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="3" y1="12" x2="21" y2="12"></line>
                    <line x1="3" y1="6" x2="21" y2="6"></line>
                    <line x1="3" y1="18" x2="21" y2="18"></line>
                </svg>
            </button>
        </div>
    </div>
</header>
