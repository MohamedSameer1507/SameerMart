# Changelog

All notable changes to the **SameerMart** e-commerce capstone project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.3.0] - 2026-10-10

### Added
- **"Aurora Indigo" 2026 Modern Design System & Theme**:
  - Replaced the high-contrast retro-brutalist theme (harsh black borders, 0px radius, flat drop shadows) with an ultra-modern, high-conversion e-commerce design system across all views.
  - Design tokens (`--primary: #4f46e5`, `--primary-gradient: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%)`, soft elevations `--shadow-sm` through `--shadow-xl`, rounded geometry `--radius-xs` to `--radius-pill`).
  - Modern typography: `Plus Jakarta Sans` paired with `Inter`.
  - Glassmorphic sticky topbar navigation with backdrop blur filter, brand logo, and active pill links.
  - Modernized AI Chat Widget: Glowing indigo/violet orb launcher with animated ping halo, rounded message bubbles, and smooth slide-up panel.
  - Dedicated modern e-commerce component classes: `.product-grid`, `.product-card`, `.product-card-media` (with subtle hover zoom), `.product-card-body`, `.product-card-title`, `.product-card-price`, `.stock-pill`, `.btn-card-action`.
  - Polished view layouts:
    - Landing page (`index.jsp`): Modern hero with gradient headlines, pill tag, feature cards, and role-oriented CTA buttons.
    - Buyer marketplace (`buyer/marketplace.jsp`): Clean responsive product grid with smooth hover lifts and stock badges.
    - Product details (`buyer/product-details.jsp`): Split media and order configuration panel with modern quantity steppers.
    - Shopping cart & checkout (`buyer/cart.jsp`, `buyer/checkout.jsp`, `buyer/order-success.jsp`): Elegant item tables, order breakdown summary card, and streamlined checkout actions.
    - Orders & Wishlist (`buyer/orders.jsp`, `buyer/wishlist.jsp`): Clean order history cards, status badges, and empty-state placeholders.
    - Seller & Admin Dashboards (`seller/dashboard.jsp`, `admin/dashboard.jsp`): Soft KPI cards, clean chart containers, and structured data tables.
  - Zero breaking changes to controller routing, form actions, or CSRF tokens; 100% automated test suite passing (244/244 tests).

---

## [1.2.0] - 2026-10-10

### Added
- **Full Controller Test Coverage (244 Tests Total)**:
  - Unit tests for remaining servlets (`LogoutServletTest`, `MarketplaceServletTest`, `SellerOrderServletTest`, `ProductImageServletTest`), bringing automated test count from 226 to **244 passing tests across 40 test classes** with 100% controller coverage.
  - Path traversal and malicious input rejection verification in `ProductImageServletTest`.
  - Seller order metrics and access authorization verification in `SellerOrderServletTest`.
- **Production Containerization**:
  - Multi-stage `Dockerfile` with Alpine Maven build and Tomcat 9 Temurin JRE runtime.
  - Automatic cloud provider dynamic `$PORT` binding support (Render, Railway, Heroku).
  - Quarantined persistent upload directory creation (`/var/sameermart/uploads`).
  - `.dockerignore` to streamline container build contexts.
- **Continuous Integration Hardening**:
  - Expanded GitHub Actions workflow trigger branches to include `antigravity/**` and `feature/**`.

### Changed
- **Logging Hygiene & Audit Compliance**:
  - Replaced all legacy `printStackTrace()` and `System.err.println()` statements across all DAOs (`CartDAO`, `OrderDAO`, `ProductDAO`, `ReviewDAO`, `UserDAO`) and servlets (`AddProductServlet`, `EditProductServlet`, `OrderServlet`, `ReviewServlet`) with structured SLF4J parameterized logging.
  - Updated `AuthFilter` lifecycle logging to use SLF4J logger.
- **Local Database Compatibility**:
  - Resolved default H2 password configuration in `DatabaseConfig` for local standalone Tomcat 9 server.

---

## [1.1.0] - 2026-10-09

### Added
- **AI Support Chatbot (`feature/ai-chatbot`)**:
  - `POST /api/v1/chat` endpoint with CSRF verification and standardized JSON envelope.
  - Multi-provider architecture with `GeminiChatProvider` (calling `gemini-3.1-flash-lite` with an 8-second HTTP timeout) and `MockChatProvider` (10+ e-commerce intent pattern fallbacks).
  - Rate limiting (sliding window of 10 requests/minute per session), 500-character input sanitization, and PII protection.
  - Floating responsive chat widget (`chat-widget.js`, `chat-widget.css`) integrated into Buyer views and landing pages.
- **Production Observability & JSON Envelopes (`feature/health-and-observability`)**:
  - `GET /api/v1/health` endpoint returning database ping, connection pool status, JVM uptime, and memory statistics.
  - Standardized JSON responses (`ApiResponse<T>`, `ApiError`) serialized via Google Gson 2.11.0.
  - `RequestIdFilter` injecting unique UUIDs into SLF4J MDC and echoing `X-Request-Id` response headers.
  - `EncodingFilter` forcing UTF-8 character encoding on all requests and responses.
  - Dual-database driver support with PostgreSQL JDBC driver (42.7.4) and automatic dialect detection in `DatabaseConfig`.
  - Structured SLF4J / Logback logging (`logback.xml`) including correlation IDs.
- **Order Lifecycle & Review Gating (`feature/order-workflow-and-reviews`)**:
  - O2 Order Status Workflow with `OrderStatusServlet` supporting state progression: `PENDING` &rarr; `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED` (or `CANCELLED`).
  - Strict HTTP 409 Conflict rejection on illegal state transitions.
  - Seller order management action buttons ("Mark Shipped", "Mark Delivered", "Cancel") with CSRF tokens and seller ownership isolation.
  - F8 Review Verification restriction: Buyers can only review products from orders with `status = 'DELIVERED'`.
  - Color-coded status badges across Seller and Buyer order lists.

### Changed
- Database seeding (`DatabaseContextListener`) now configurable via environment variables (`ADMIN_NAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`) preventing committed hardcoded admin passwords.
- Upgraded test suite from 113 to 153 unit and integration tests with 100% pass rate.

---

## [1.0.0] - 2026-09-21

### Added
- **Baseline Architecture & Core E-Commerce Modules (F1–F8)**:
  - **F1 User Authentication**: Role-based access control (BUYER, SELLER, ADMIN) with jBCrypt 12-round password hashing and session management.
  - **F2 Product Catalog & Search**: Marketplace view with keyword search, category filtering, and active listing constraints.
  - **F3 Seller Inventory Management**: Multipart product creation and editing with stock controls and external file uploads.
  - **F4 Shopping Cart**: Persistent cart per buyer with dynamic quantity adjustments and stock limits.
  - **F5 Atomic Order Processing**: Multi-item cart checkout executed within a single database transaction (`setAutoCommit(false)`, locking, stock decrement, cart clearing).
  - **F6 Seller Fulfillment**: Seller order isolation view showing purchased items, buyer details, and calculated earnings.
  - **F7 Platform Administration**: Admin dashboard displaying platform-wide revenue and product moderation controls.
  - **F8 Product Reviews**: 1–5 star ratings and reviews with HTML sanitization preventing stored XSS.
- **Deployment Hardening**:
  - HikariCP connection pooling configured for local H2 file storage and production PostgreSQL.
  - Per-session 32-byte cryptographically secure CSRF protection (`CsrfUtil`) across all state-changing POST forms.
  - Externalized image storage directory (`SAMEERMART_UPLOAD_DIR`) with UUID filename sanitization preventing path traversal attacks.
  - `ProductImageServlet` with strict path validation and content-type streaming.
- **Automated Test Suite**:
  - 113 JUnit tests covering DAO transactions, authentication filters, CSRF validation, XSS prevention, and business rules.

---

## [0.1.0] - 2026-08-28

### Added
- Initial project scaffolding for Anna University R2025 Semester 3 Java Capstone.
- Maven POM configuration targeting Java 17 and Apache Tomcat 9 (`javax.servlet`).
- Minimalist neo-brutalist CSS design system (`style.css`).
