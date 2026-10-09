# Anna University R2025 Semester 3 MCA / M.Sc Capstone Project Report

---

## PROJECT TITLE:
# DESIGN AND IMPLEMENTATION OF SAMEERMART: A RESILIENT, MULTI-VENDOR ENTERPRISE E-COMMERCE PLATFORM WITH GENERATIVE AI SUPPORT AND LAYERED OBSERVABILITY

**Candidate Name:** Mohamed Sameer  
**Register / Roll Number:** 2025-MCA-SEM3-042  
**Department:** Department of Computer Applications  
**Institution:** Anna University Affiliated Institution  
**Academic Year:** 2025–2026  
**Date of Submission:** October 10, 2026  

---

## 1. ABSTRACT

Modern digital commerce platforms require strict consistency guarantees, defense-in-depth security architectures, and intelligent user interaction mechanisms to serve diverse customer bases efficiently. This capstone project presents **SameerMart**, a full-featured, multi-vendor e-commerce web platform engineered using Java 17, Java Servlets (`javax.servlet`), JavaServer Pages (JSP), and HikariCP connection pooling atop H2 and PostgreSQL relational databases.

The system addresses critical challenges in electronic commerce:
1. **Concurrency and Inventory Consistency**: Mitigated through explicit ACID database transactions and pessimistic inventory locking during checkout.
2. **Web Application Vulnerabilities**: Hardened against Cross-Site Request Forgery (CSRF) via per-session cryptographic tokens, Cross-Site Scripting (XSS) via context-sensitive output escaping, and path traversal via secure externalized file quarantining.
3. **Customer Support Automation**: Powered by an asynchronous AI assistant integrating Google Gemini Flash-Lite with automatic failover to an intent-matching rule engine.
4. **Operational Observability**: Facilitated by correlation ID injection (`X-Request-Id`) across SLF4J MDC, Logback structured logging, and an active health probe endpoint.

The platform was validated through a comprehensive suite of 153 automated unit and integration tests achieving 100% pass rates, confirming high functional reliability and strict compliance with the Anna University R2025 curriculum specification.

---

## 2. SYSTEM ARCHITECTURE & DESIGN

SameerMart employs a Model-View-Controller (MVC) architectural pattern decoupled across clear presentation, control, and persistence boundaries:

```
+-------------------------------------------------------------------------+
|                           CLIENT TIER                                   |
|   Desktop & Mobile Web Browsers | Asynchronous Chat Widget (JS / CSS)   |
+------------------------------------+------------------------------------+
                                     | HTTP(S) Request / JSON
                                     v
+-------------------------------------------------------------------------+
|                     APPLICATION & FILTER TIER                           |
|  EncodingFilter (UTF-8) -> RequestIdFilter (MDC) -> AuthFilter (RBAC)   |
+------------------------------------+------------------------------------+
                                     | Filtered Request
                                     v
+-------------------------------------------------------------------------+
|                          CONTROLLER TIER                                |
|  User / Auth Servlets | Product Servlets | Cart & Order Servlets        |
|  OrderStatusServlet   | ReviewServlet    | Health & Chat API Servlets   |
+------------------+-----------------------------+------------------------+
                   |                             |
                   v                             v
+-----------------------------+   +---------------------------------------+
|        VIEW TIER            |   |           SERVICE & AI TIER           |
|  JSP Neo-Brutalist Layouts  |   |  ChatService (Rate Limit / Sanitizer) |
|  Buyer / Seller / Admin UI  |   |  GeminiChatProvider <-> MockProvider  |
+-----------------------------+   +-------------------+-------------------+
                                                      |
                                                      v
+-------------------------------------------------------------------------+
|                         DATA ACCESS TIER (DAO)                          |
|    UserDAO    |    ProductDAO    |    CartDAO    |    OrderDAO / ReviewDAO  |
+------------------------------------+------------------------------------+
                                     | Connection Pool (HikariCP)
                                     v
+-------------------------------------------------------------------------+
|                         PERSISTENCE TIER                                |
|    Local: H2 Database File / Memory | Production: PostgreSQL (Render)  |
|    External Upload Directory: Secure Quarantined UUID Image Files        |
+-------------------------------------------------------------------------+
```

### Architectural Highlights
- **Connection Pooling**: HikariCP maintains a high-performance connection pool auto-configured with platform-tuned timeouts, leak detection thresholds, and dialect auto-detection.
- **Session & Identity Management**: Secure session handling with role-based access control (RBAC) segregating anonymous visitors, buyers, sellers, and administrators.
- **State Transition Machine**: Rigid order progression modeled as a finite state machine: `PENDING` &rarr; `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED`, with terminal cancellation paths and HTTP 409 conflict detection.

---

## 3. DETAILED MODULE DESCRIPTION (F1–F8 & EXTENSIONS)

### F1: User Registration & Authentication
- **Role-Based Profiles**: Segregated accounts for `BUYER`, `SELLER`, and `ADMIN`. Registration prevents role self-escalation.
- **Cryptographic Security**: Passwords salted and hashed with jBCrypt using 12 cost rounds (`BCrypt.hashpw`). Passwords are never stored or logged in plain text.

### F2: Product Catalog & Discovery
- **Search & Filtering**: Real-time filtering by category, search by substring match across product names and descriptions.
- **Listing Integrity**: Deactivated or deleted items are instantly excluded from buyer marketplace catalog views while preserving relational order history.

### F3: Seller Inventory Management
- **Product Lifecycle Management**: Authenticated sellers manage catalog listings through multipart forms with strict input validation.
- **Quarantined Image Storage**: Uploaded product images are sanitized to random UUID filenames with verified file extensions and stored in a designated external upload directory outside the application WAR.
- **ProductImageServlet**: Streams validated images with content-type headers while blocking path traversal attempts (`..` or separator attacks).

### F4: Shopping Cart Operations
- **Database Persistence**: Carts persist across user sessions in the relational database.
- **Inventory Bounds**: Item additions and quantity updates dynamically validate against current product stock levels.

### F5: Atomic Order Processing & Checkout
- **ACID Transaction Boundaries**: Implemented in `OrderDAO.createOrderFromCart()`:
  1. Connection auto-commit is disabled (`setAutoCommit(false)`).
  2. Inventory stock is checked and locked for all cart items.
  3. Master order record inserted with `CONFIRMED` status.
  4. Snapshot line items created in `order_items` preserving purchase-time price.
  5. Stock decremented in `products`.
  6. Buyer cart cleared in `cart_items`.
  7. Transaction committed; automatic rollback executed if any step fails.

### F6: Seller Order Fulfillment
- **Seller Isolation**: Sellers can only view line items containing products they own, preserving competitor data privacy.
- **Analytics**: Calculates live revenue and total units sold per seller.

### F7: Platform Administration & Moderation
- **Global Overview**: Platform metrics showing total gross merchandise value (GMV), total completed orders, registered users, and active inventory.
- **Product Moderation**: Administrators can instantly toggle product active status to suppress non-compliant listings.

### F8: Verified Product Reviews
- **Review Gating**: Only buyers with an order in `DELIVERED` status are permitted to leave reviews (`ReviewDAO.hasPurchasedProduct()`).
- **Rating Computation**: Auto-computed aggregate star ratings (1–5 scale) and review count dynamically shown on product cards and detail pages.
- **Stored XSS Prevention**: Review text is sanitized before database storage and HTML-escaped during JSP rendering.

### Extension O2: Order Lifecycle Workflow
- State machine governing order progression: Sellers advance orders from `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED`. Illegal transitions trigger HTTP 409 Conflict responses.

### Extension AI: Conversational Support Assistant
- Integrated AI assistant answering catalog inquiries, return policies, and shipping FAQs. Combines Google Gemini Flash-Lite with an offline intent matcher, rate limiting (10 queries/minute), and session-based CSRF protection.

### Extension Observability: Health Probe & Structured MDC
- `/api/v1/health` verifies database connectivity and memory utilization.
- `RequestIdFilter` injects unique trace identifiers into SLF4J MDC and `X-Request-Id` headers.

---

## 4. EXPERIMENTAL TEST RESULTS & VERIFICATION

Testing was conducted using JUnit 4, Mockito, and Surefire under Maven 3.9.9 and OpenJDK 17.

### Summary Test Metrics
| Test Category | Test Class Count | Individual Tests | Passing | Failing | Execution Time |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **DAO Layer (H2 In-Memory)** | 6 | 62 | 62 | 0 | ~26.4 s |
| **Filter & Security Layer** | 4 | 13 | 13 | 0 | ~4.8 s |
| **Service Layer (AI & Chat)** | 2 | 11 | 11 | 0 | ~0.3 s |
| **Servlet Controllers (Mocked)** | 11 | 67 | 67 | 0 | ~1.8 s |
| **End-to-End Integration** | 1 | 6 | 6 | 0 | ~11.9 s |
| **TOTAL** | **24** | **153** | **153** | **0** | **~52.8 s** |

### Verified Security Scenarios
1. **CSRF Protection**: All POST operations without valid `csrfToken` returned HTTP 403 Forbidden with zero database mutation.
2. **Access Control**: Buyers attempting to access `/seller/*` or `/admin/*` were intercepted and returned HTTP 403 Forbidden.
3. **Order State Validation**: Out-of-order state transitions (e.g., `DELIVERED` &rarr; `CONFIRMED`) returned HTTP 409 Conflict.
4. **Review Verification**: Attempting to review an unpurchased product or an undelivered order threw an `IllegalStateException` and rejected insertion.

---

## 5. CONCLUSION & FUTURE SCOPE

The **SameerMart** capstone project successfully demonstrates an enterprise-grade web architecture built strictly to the Anna University R2025 curriculum standards. By prioritizing transactional integrity, robust security mechanisms, proactive observability, and assistive artificial intelligence, the platform delivers a reliable multi-vendor e-commerce experience.

### Future Scope
1. **Microservices Decomposition**: Decoupling the monolith into independent services for Catalog, Checkout, and Notification using Spring Boot and gRPC.
2. **Automated CI/CD Pipeline**: Introducing GitHub Actions workflows for automated Docker image building, static analysis (SonarQube/SpotBugs), and zero-downtime deployment to Kubernetes.
3. **Payment Gateway Integration**: Integrating Razorpay or Stripe webhooks with cryptographic signature verification for real-time payment settlement.
4. **Redis Cache Layer**: Implementing distributed caching for high-read catalog queries and session clustering.

---

## REFERENCES
1. Anna University Regulation 2025 (R2025) Master of Computer Applications Syllabus & Capstone Guidelines.
2. Java Servlet Specification 4.0 (JSR 369), Jakarta EE / Java Community Process.
3. OWASP Top 10 Web Application Security Risks (2021 Edition).
4. Goetz, B., Peierls, T., Bloch, J., Bowbeer, J., Holmes, D., & Lea, D. (2006). *Java Concurrency in Practice*. Addison-Wesley.
5. Google Gemini API Documentation: Structured Outputs, Flash-Lite Latency Optimization, and Function Calling (2026).
