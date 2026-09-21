<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${product.name}"/> | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp">
        <jsp:param name="activeNav" value="products" />
    </jsp:include>

    <main class="main-content">
        <div class="container">
            <div style="margin-bottom: 20px;">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-sm">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="19" y1="12" x2="5" y2="12"></line>
                        <polyline points="12 19 5 12 12 5"></polyline>
                    </svg>
                    Back to Products
                </a>
            </div>

            <div class="product-detail-layout">
                <!-- Product Image Section -->
                <div class="detail-img-box">
                    <c:choose>
                        <c:when test="${not empty product.imageUrl}">
                            <img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>">
                        </c:when>
                        <c:otherwise>
                            <div style="text-align: center; color: #94A3B8;">
                                <svg width="84" height="84" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                                    <line x1="8" y1="21" x2="16" y2="21"></line>
                                    <line x1="12" y1="17" x2="12" y2="21"></line>
                                </svg>
                                <p style="margin-top: 10px; font-size: 0.9rem;">Product Preview</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Product Info & Purchase Form -->
                <div>
                    <span class="badge badge-blue" style="margin-bottom: 8px;">
                        <c:out value="${product.category}"/>
                    </span>
                    <h1 style="font-size: 2rem; font-weight: 800; color: var(--dark); margin-bottom: 12px; line-height: 1.2;">
                        <c:out value="${product.name}"/>
                    </h1>

                    <div style="display: flex; align-items: baseline; gap: 16px; margin-bottom: 20px; padding-bottom: 18px; border-bottom: 1px solid var(--border);">
                        <span style="font-size: 2.2rem; font-weight: 800; color: var(--primary);">
                            &#8377;<fmt:formatNumber value="${product.priceMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/>
                        </span>
                        <span class="badge badge-green">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                                <polyline points="20 6 9 17 4 12"></polyline>
                            </svg>
                            <c:out value="${product.stockQuantity}"/> units in stock
                        </span>
                    </div>

                    <div style="margin-bottom: 28px;">
                        <h4 style="font-size: 0.9rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; color: var(--text-muted); margin-bottom: 8px;">
                            Description
                        </h4>
                        <p style="font-size: 1rem; color: var(--text); line-height: 1.7;">
                            <c:out value="${product.description}"/>
                        </p>
                    </div>

                    <!-- Add to Cart Form -->
                    <form method="post" action="${pageContext.request.contextPath}/cart/add" style="background-color: #F8FAFC; border: 1px solid var(--border); border-radius: var(--radius); padding: 24px; margin-bottom: 28px;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <input type="hidden" name="productId" value="${product.id}">

                        <div class="form-group" style="margin-bottom: 20px;">
                            <label for="quantity" class="form-label">Select Quantity</label>
                            <div style="display: flex; align-items: center; gap: 12px;">
                                <input id="quantity" name="quantity" type="number" min="1" max="${product.stockQuantity}" value="1" required class="form-control" style="max-width: 110px; font-size: 1.1rem; text-align: center; font-weight: 700;">
                                <span style="font-size: 0.85rem; color: var(--text-muted);">Max order: <c:out value="${product.stockQuantity}"/></span>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-accent btn-lg btn-block">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
                                <line x1="3" y1="6" x2="21" y2="6"></line>
                                <path d="M16 10a4 4 0 0 1-8 0"></path>
                            </svg>
                            Add to Shopping Cart
                        </button>
                    </form>

                    <!-- Trust Signals -->
                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 14px;">
                        <div style="display: flex; align-items: center; gap: 10px; font-size: 0.85rem; color: var(--text-muted);">
                            <span style="color: var(--primary);">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                                </svg>
                            </span>
                            <span>100% Genuine Guaranteed</span>
                        </div>
                        <div style="display: flex; align-items: center; gap: 10px; font-size: 0.85rem; color: var(--text-muted);">
                            <span style="color: var(--primary);">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="1" y="3" width="15" height="13"></rect>
                                    <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                                    <circle cx="5.5" cy="18.5" r="2.5"></circle>
                                    <circle cx="18.5" cy="18.5" r="2.5"></circle>
                                </svg>
                            </span>
                            <span>Quick Doorstep Delivery</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
