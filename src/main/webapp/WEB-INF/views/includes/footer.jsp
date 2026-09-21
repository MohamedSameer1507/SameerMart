<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<footer class="footer">
    <div class="container">
        <div class="footer-grid">
            <div class="footer-brand">
                <div style="display:flex; align-items:center; gap:10px; margin-bottom:14px;">
                    <span class="brand-icon" style="width:32px; height:32px;">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="9" cy="21" r="1"></circle>
                            <circle cx="20" cy="21" r="1"></circle>
                            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                        </svg>
                    </span>
                    <h3 style="margin:0; font-size:1.3rem;">Sameer<span style="color:var(--accent);">Mart</span></h3>
                </div>
                <p>Your premier online marketplace for electronics, fashion, essentials, and home appliances with fast delivery and secure checkout.</p>
                <div style="display:flex; gap:12px; margin-top:16px;">
                    <span class="badge badge-blue" style="background:rgba(21,101,192,0.2); color:#93C5FD;">100% Genuine</span>
                    <span class="badge badge-red" style="background:rgba(229,57,53,0.2); color:#FCA5A5;">Fast Delivery</span>
                </div>
            </div>

            <div class="footer-col">
                <h4>Quick Links</h4>
                <ul class="footer-links">
                    <li><a href="${pageContext.request.contextPath}/products">Browse Products</a></li>
                    <li><a href="${pageContext.request.contextPath}/cart">Shopping Cart</a></li>
                    <li><a href="${pageContext.request.contextPath}/dashboard">My Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/login">Sign In</a></li>
                    <li><a href="${pageContext.request.contextPath}/register">Create Account</a></li>
                </ul>
            </div>

            <div class="footer-col">
                <h4>Customer Care</h4>
                <ul class="footer-links">
                    <li><a href="#">Help & Support</a></li>
                    <li><a href="#">Shipping Policy</a></li>
                    <li><a href="#">Returns & Refunds</a></li>
                    <li><a href="#">Privacy Policy</a></li>
                    <li><a href="#">Terms of Service</a></li>
                </ul>
            </div>

            <div class="footer-col">
                <h4>Platform & Contact</h4>
                <p style="font-size:0.85rem; color:#94A3B8; margin-bottom:12px;">
                    Powered by Spring Boot 3.3.4, Java 21, and persistent H2 Database with Session Fixation & CSRF protection.
                </p>
                <p style="font-size:0.85rem; color:#E2E8F0;">
                    <strong>Email:</strong> support@sameermart.com<br>
                    <strong>Location:</strong> India
                </p>
            </div>
        </div>

        <div class="footer-bottom">
            <div>&copy; 2026 <strong>SameerMart</strong>. All rights reserved.</div>
            <div style="color:#64748B;">Built with modern Blue & Red Design System</div>
        </div>
    </div>
</footer>
