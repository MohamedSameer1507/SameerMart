<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart | SameerMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>
    <jsp:include page="includes/navbar.jsp">
        <jsp:param name="activeNav" value="cart" />
    </jsp:include>

    <main class="main-content">
        <div class="container">
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 28px;">
                <div>
                    <h1 style="font-size: 1.85rem; font-weight: 800; color: var(--dark); margin-bottom: 4px;">Shopping Cart</h1>
                    <p style="color: var(--text-muted); font-size: 0.95rem;">Review your selected items before proceeding to checkout</p>
                </div>
                <a href="${pageContext.request.contextPath}/products" class="btn btn-outline btn-sm">
                    Continue Shopping
                </a>
            </div>

            <c:choose>
                <c:when test="${empty cart.items}">
                    <div class="card" style="text-align: center; padding: 64px 24px; max-width: 600px; margin: 0 auto;">
                        <div style="width: 72px; height: 72px; background-color: #EFF6FF; border-radius: 50%; display: inline-flex; align-items: center; justify-content: center; color: var(--primary); margin-bottom: 20px;">
                            <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="9" cy="21" r="1"></circle>
                                <circle cx="20" cy="21" r="1"></circle>
                                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                            </svg>
                        </div>
                        <h2 style="font-size: 1.4rem; font-weight: 700; color: var(--dark); margin-bottom: 8px;">Your cart is currently empty</h2>
                        <p style="color: var(--text-muted); font-size: 0.95rem; margin-bottom: 24px;">Looks like you haven't added any items to your shopping cart yet.</p>
                        <div>
                            <a href="${pageContext.request.contextPath}/products" class="btn btn-accent btn-lg" style="display: inline-flex;">
                                Explore Products
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="two-column-layout">
                        <!-- Items Table Card -->
                        <div class="card">
                            <div class="card-header">
                                <h2 class="card-title" style="font-size: 1.1rem;">Cart Items</h2>
                            </div>
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Product</th>
                                            <th>Price</th>
                                            <th>Quantity</th>
                                            <th>Subtotal</th>
                                            <th>Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${cart.items}" var="item">
                                            <tr>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/products/${item.productId}" style="font-weight: 600; color: var(--dark);">
                                                        <c:out value="${item.name}"/>
                                                    </a>
                                                    <div style="font-size: 0.8rem; color: var(--text-muted);">ID: #${item.productId}</div>
                                                </td>
                                                <td style="font-weight: 600;">
                                                    &#8377;<fmt:formatNumber value="${item.unitPriceMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/>
                                                </td>
                                                <td>
                                                    <form method="post" action="${pageContext.request.contextPath}/cart/update" class="qty-form">
                                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                                        <input type="hidden" name="productId" value="${item.productId}">
                                                        <input name="quantity" type="number" min="0" max="${item.availableStock}" value="${item.quantity}" required class="qty-input">
                                                        <button type="submit" class="btn btn-outline btn-sm" title="Update Quantity">
                                                            Update
                                                        </button>
                                                    </form>
                                                </td>
                                                <td style="font-weight: 700; color: var(--primary);">
                                                    &#8377;<fmt:formatNumber value="${item.lineTotalMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/>
                                                </td>
                                                <td>
                                                    <form method="post" action="${pageContext.request.contextPath}/cart/remove" style="margin: 0;">
                                                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                                        <input type="hidden" name="productId" value="${item.productId}">
                                                        <button type="submit" class="btn btn-outline-danger btn-sm" title="Remove item">
                                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                <polyline points="3 6 5 6 21 6"></polyline>
                                                                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                                            </svg>
                                                            Remove
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Order Summary Card -->
                        <div class="card">
                            <div class="card-header">
                                <h2 class="card-title" style="font-size: 1.1rem;">Order Summary</h2>
                            </div>
                            <div class="card-body">
                                <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 0.95rem;">
                                    <span style="color: var(--text-muted);">Items Subtotal:</span>
                                    <span style="font-weight: 600;">&#8377;<fmt:formatNumber value="${cart.totalMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/></span>
                                </div>
                                <div style="display: flex; justify-content: space-between; margin-bottom: 12px; font-size: 0.95rem;">
                                    <span style="color: var(--text-muted);">Delivery Charge:</span>
                                    <span style="font-weight: 600; color: var(--success);">FREE</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; margin-bottom: 20px; font-size: 0.95rem;">
                                    <span style="color: var(--text-muted);">Estimated Tax:</span>
                                    <span style="font-weight: 600; color: var(--text-muted);">&#8377;0.00</span>
                                </div>
                                <div style="display: flex; justify-content: space-between; align-items: baseline; padding-top: 16px; border-top: 2px dashed var(--border); margin-bottom: 24px;">
                                    <span style="font-size: 1.1rem; font-weight: 700; color: var(--dark);">Grand Total:</span>
                                    <span style="font-size: 1.6rem; font-weight: 800; color: var(--primary);">&#8377;<fmt:formatNumber value="${cart.totalMinor / 100.0}" minFractionDigits="2" maxFractionDigits="2"/></span>
                                </div>
                                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-accent btn-lg btn-block">
                                    Proceed to Checkout
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                                        <line x1="5" y1="12" x2="19" y2="12"></line>
                                        <polyline points="12 5 19 12 12 19"></polyline>
                                    </svg>
                                </a>
                            </div>
                            <div class="card-footer" style="text-align: center; font-size: 0.82rem; color: var(--text-muted);">
                                🔒 Secure SSL checkout with session protection
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <jsp:include page="includes/footer.jsp" />
</body>
</html>
