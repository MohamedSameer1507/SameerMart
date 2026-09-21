<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp">
        <jsp:param name="activeNav" value="products" />
    </jsp:include>

    <main class="main-content">
        <div class="container">
            <!-- Hero Section -->
            <section class="hero">
                <div class="hero-grid">
                    <div class="hero-content">
                        <span class="badge badge-blue" style="margin-bottom: 12px;">Featured Marketplace</span>
                        <h1>Shop Top Quality at <span class="brand-blue">Sameer</span><span class="brand-red">Mart</span></h1>
                        <p class="hero-lead">Discover premium tech, fashion, essentials, and home appliances. Enjoy instant simulated checkout with verified sellers.</p>
                        <div class="hero-actions">
                            <a href="#productCatalog" class="btn btn-accent btn-lg">Explore Catalog</a>
                            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary btn-lg">View Shopping Cart</a>
                        </div>
                        <div class="hero-stats">
                            <div class="hero-stat-item">
                                <strong>100%</strong>
                                <span>Verified Items</span>
                            </div>
                            <div class="hero-stat-item">
                                <strong>Instant</strong>
                                <span>Fast Delivery</span>
                            </div>
                            <div class="hero-stat-item">
                                <strong>Secure</strong>
                                <span>CSRF & Session Safe</span>
                            </div>
                        </div>
                    </div>
                    <div class="hero-card-preview">
                        <div class="hero-card-top">
                            <span style="font-weight: 800; font-size: 1.1rem; letter-spacing: 0.5px;">SameerMart Card</span>
                            <span class="hero-card-chip">PRIME</span>
                        </div>
                        <div class="hero-card-number">&bull;&bull;&bull;&bull; &bull;&bull;&bull;&bull; &bull;&bull;&bull;&bull; 2026</div>
                        <div class="hero-card-bottom">
                            <div>
                                <div style="font-size: 0.7rem; text-transform: uppercase; opacity: 0.7;">Verified Member</div>
                                <div style="font-weight: 600; font-size: 0.95rem;">SameerMart Buyer</div>
                            </div>
                            <div>
                                <div style="font-size: 0.7rem; text-transform: uppercase; opacity: 0.7;">Status</div>
                                <div style="font-weight: 600; font-size: 0.95rem; color: #86EFAC;">ACTIVE</div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- Search and Filter Bar -->
            <div class="filter-bar" id="productCatalog">
                <form method="get" action="${pageContext.request.contextPath}/products" class="filter-form">
                    <div class="search-input-group">
                        <span class="search-icon-inside">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="11" cy="11" r="8"></circle>
                                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                            </svg>
                        </span>
                        <input type="search" name="keyword" value="<c:out value='${keyword}'/>" class="search-input" placeholder="Search by name or description...">
                    </div>

                    <select name="category" class="category-select">
                        <option value="">All Categories</option>
                        <c:forEach items="${categories}" var="item">
                            <option value="<c:out value='${item}'/>" ${item eq selectedCategory ? 'selected' : ''}>
                                <c:out value="${item}"/>
                            </option>
                        </c:forEach>
                    </select>

                    <button type="submit" class="btn btn-primary">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"></polygon>
                        </svg>
                        Filter
                    </button>

                    <c:if test="${not empty keyword or not empty selectedCategory}">
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline">Reset</a>
                    </c:if>
                </form>
            </div>

            <!-- Product Grid -->
            <c:choose>
                <c:when test="${empty products}">
                    <div class="card" style="text-align: center; padding: 48px 24px;">
                        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#94A3B8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="margin-bottom: 16px;">
                            <circle cx="11" cy="11" r="8"></circle>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                        </svg>
                        <h3 style="color: var(--dark); margin-bottom: 8px;">No products match your criteria</h3>
                        <p style="color: var(--text-muted); margin-bottom: 20px;">Try adjusting your keyword or category filters to find what you are looking for.</p>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary" style="display: inline-flex; width: auto;">Show All Products</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="product-grid">
                        <c:forEach items="${products}" var="product">
                            <div class="product-card">
                                <div class="product-image-wrap">
                                    <c:choose>
                                        <c:when test="${not empty product.imageUrl}">
                                            <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" loading="lazy">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="product-image-placeholder">
                                                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                                    <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                                                    <line x1="8" y1="21" x2="16" y2="21"></line>
                                                    <line x1="12" y1="17" x2="12" y2="21"></line>
                                                </svg>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="product-body">
                                    <span class="product-category-chip"><c:out value="${product.category}"/></span>
                                    <h3 class="product-title">
                                        <a href="${pageContext.request.contextPath}/products/${product.id}">
                                            <c:out value="${product.name}"/>
                                        </a>
                                    </h3>
                                    <div class="product-price-row">
                                        <div class="product-price">
                                            &#8377;<fmt:formatNumber value="${product.priceMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/>
                                        </div>
                                        <span class="stock-tag">In Stock</span>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/products/${product.id}" class="btn btn-outline-primary btn-sm btn-block" style="margin-top: 14px;">
                                        View Details
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
