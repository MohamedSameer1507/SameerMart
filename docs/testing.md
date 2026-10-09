# SameerMart — Comprehensive Testing & Security Penetration Audit Report

This document records the automated testing strategy, complete test inventory, static code analysis audit, and formal security penetration self-audit for **SameerMart** (Java 17, Servlets/JSP, Tomcat 9, Maven, HikariCP, H2/PostgreSQL).

Prepared for the **Anna University R2025 Semester 3 Java Capstone Review (October 10, 2026)**.

---

## 1. How to Run the Verification Suite

### Automated Test Suite Execution
```bash
# Run all 226 unit and integration tests under JUnit 5 Jupiter
mvn clean test

# Run tests, static analysis (Checkstyle + SpotBugs), and package WAR
mvn clean verify

# Produce standalone WAR artifact
mvn clean package
```

### Static Analysis Audits
```bash
# Checkstyle code formatting and style compliance audit
mvn checkstyle:check

# SpotBugs bytecode bug pattern and security detector audit
mvn spotbugs:check
```

### Database Environment
All automated tests execute against an isolated **in-memory H2 database** (`jdbc:h2:mem:sameermart_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1`) managed by HikariCP. Each test class starts from an isolated or initialized schema without mutating any persistent files or remote databases.

---

## 2. Test Execution Summary

| Metric | Result | Compliance Target | Status |
| :--- | :---: | :---: | :---: |
| **Total Test Classes** | **36** | ≥ 20 | **PASS** |
| **Total Automated Tests** | **226** | ≥ 113 | **PASS (100% Increase)** |
| **Failures** | **0** | 0 | **PASS** |
| **Errors** | **0** | 0 | **PASS** |
| **Skipped** | **0** | 0 | **PASS** |
| **Test Framework** | **JUnit 5 (Jupiter 5.10.2)** | JUnit 5 | **PASS** |
| **Checkstyle Violations** | **0** | 0 | **PASS** |
| **SpotBugs Bugs / Warnings** | **0** | 0 | **PASS** |
| **Average Suite Duration** | **~22.2 seconds** | < 60s | **PASS** |

---

## 3. Complete Test Inventory (226 Tests across 36 Classes)

### 3.1 Data Access Tier (DAO Tests — 44 Tests)
All DAO tests run against real in-memory SQL tables via HikariCP, verifying connection lifecycle, query execution, constraint handling, and transactions.

| Class | Tests | Key Capabilities Verified |
| :--- | :---: | :--- |
| `UserDAOTest` | 6 | User registration, email lookup, unique constraint enforcement, BCrypt password hashing validation, verification logic. |
| `ProductDAOTest` | 5 | Product insertion, updates, deletion, active/available status filtering, seller-scoped listing queries. |
| `CartDAOTest` | 4 | Add to cart, quantity increments, quantity update capped by available inventory, item removal, cart clearance. |
| `OrderDAOTest` | 9 | Atomic transactional checkout, stock deduction, rollback on insufficient stock, buyer order history, seller order segregation, units sold aggregation, status distribution map, top-selling product ranking, category sales breakdown. |
| `ReviewDAOTest` | 8 | Verified buyer purchase eligibility check (`DELIVERED` status required), single review per buyer-product constraint, 1–5 star rating bounds, average rating calculation, XSS/SQLi inert storage. |
| `WishlistDAOTest` | 6 | Add to wishlist, duplicate avoidance (`ON CONFLICT DO NOTHING`), removal by product ID, buyer wishlist retrieval with product details, presence check, item count. |
| `MockChatProviderTest` | 11 | Fallback rule-engine responses for catalog questions, shipping, returns, payment methods, fallback replies, domain context enrichment. |

### 3.2 Domain Model & Business Rules (5 Tests)
| Class | Tests | Key Capabilities Verified |
| :--- | :---: | :--- |
| `BusinessRuleTest` | 5 | Invariants for subtotal arithmetic, negative stock prevention, active listing status defaults, user role validation. |

### 3.3 Service Tier (44 Tests)
Validates business logic decoupling, custom domain exceptions (`ValidationException`, `ResourceNotFoundException`, `UnauthorizedException`, `ConflictException`), transaction handling, and rate limiting.

| Class | Tests | Key Capabilities Verified |
| :--- | :---: | :--- |
| `AuthServiceTest` | 6 | Credential verification, registration validation, duplicate email prevention, role sanitization, password hashing via BCrypt. |
| `ProductServiceTest` | 6 | Product creation, seller ownership validation, active status filtering, pricing validation, stock updates. |
| `CartServiceTest` | 6 | Cart operations, quantity validation, stock limit enforcement, total subtotal computation. |
| `OrderServiceTest` | 6 | Atomic multi-item checkout, transaction rollback, buyer order ownership, status transition authorization. |
| `ReviewServiceTest` | 5 | Delivered purchase verification, rating range enforcement (1–5), duplicate review rejection, review summary computation. |
| `WishlistServiceTest` | 9 | Add item, remove item, retrieve wishlist items, item count check, duplicate handling, parameter validation. |
| `ChatServiceTest` | 6 | Session sliding-window rate limiting (10 msg/min), 500-char message length cap, catalog context enrichment, provider failover fallback, cache eviction. |

### 3.4 Security & Filter Tier (16 Tests)
| Class | Tests | Key Capabilities Verified |
| :--- | :---: | :--- |
| `AuthFilterTest` | 7 | Unauthenticated requests redirected to `/login.jsp`, malformed/incomplete session rejection, buyer blocked from `/seller/*` (403), seller blocked from `/admin/*` (403), legitimate role access allowed (`/buyer/*`, `/seller/*`, `/admin/*`). |
| `SecurityTest` | 6 | Classic SQL injection payload rejection via `PreparedStatement`, XSS payload escaping via `HtmlUtil`, BCrypt 60-char hash and cost factor 12 verification, CSRF constant-time comparison defense, CSRF token validation workflow (missing/tampered rejection), CSRF token entropy/uniqueness. |
| `RequestIdFilterTest` | 2 | `X-Request-Id` generation, injection into SLF4J MDC, propagation in response header, CRLF injection sanitization (`^[A-Za-z0-9_-]{1,64}$`). |
| `EncodingFilterTest` | 1 | UTF-8 request and response character encoding enforcement across all servlets. |

### 3.5 Controller & Servlet Tier (111 Tests)
| Class | Tests | Key Capabilities Verified |
| :--- | :---: | :--- |
| `LoginServletTest` | 9 | GET login page redirect, missing parameter handling, invalid email/password handling, session fixation mitigation (`changeSessionId`), role-based redirection (BUYER &rarr; `/buyer/marketplace`, SELLER &rarr; `/seller/dashboard.jsp`, ADMIN &rarr; `/admin/dashboard.jsp`), sliding-window brute-force rate limiting (locks after 5 failures for 5 minutes), counter reset on successful authentication. |
| `RegisterServletTest` | 7 | Public role whitelist enforcement (rejects forged ADMIN registrations), email format validation, password length checks, duplicate email conflict handling. |
| `CartServletTest` | 7 | Role verification (buyers only), quantity bounds checking, add/update/remove items, CSRF validation. |
| `OrderServletTest` | 9 | Empty cart rejection, buyer checkout, ownership protection (buyers cannot inspect other buyers' orders), CSRF validation. |
| `OrderStatusServletTest` | 12 | State transition validation (`CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED`), seller item authorization, illegal state transition rejection (HTTP 409 Conflict), cancellation rules, CSRF validation. |
| `ProductDetailsServletTest` | 7 | Product ID validation, inactive product hiding from buyers, review summary population, rating calculation. |
| `EditProductServletTest` | 7 | Seller ownership enforcement (sellers cannot edit competitors' products), pricing/stock bounds, CSRF token check. |
| `DeleteProductServletTest` | 5 | Seller ownership enforcement (sellers cannot delete competitors' products), CSRF token check. |
| `AdminProductStatusServletTest` | 4 | Role enforcement (non-admin receives HTTP 403 Forbidden), product activation/deactivation toggling. |
| `ReviewServletTest` | 12 | Buyer authentication check, delivered purchase eligibility check, duplicate review rejection, rating range check (1–5), 500-char review length cap, CSRF validation. |
| `WishlistServletTest` | 7 | Role check (buyers only), add to wishlist, remove from wishlist, duplicate handling, redirect to referer/wishlist page, CSRF validation. |
| `HealthServletTest` | 2 | `/api/v1/health` JSON response verification: UP status when DB is connected, 503 DOWN response when DB is unreachable. |
| `ChatServletTest` | 5 | Asynchronous JSON chat API: rate limit handling (HTTP 429), input length validation, JSON response format, CSRF validation. |
| `ProductApiServletTest` | 6 | REST API `GET /api/v1/products`: list all available products, filter by category, search by keyword, lookup by ID, 404 for unknown product, JSON envelope. |
| `CartApiServletTest` | 3 | REST API `GET /api/v1/cart`: authenticated buyer cart retrieval, unauthenticated 401 rejection, JSON envelope. |
| `OrderApiServletTest` | 4 | REST API `GET /api/v1/orders`: buyer orders list, seller orders list, admin all orders list, unauthenticated 401 rejection. |

### 3.6 End-to-End Integration Flow (6 Tests)
| Class | Tests | Key Capabilities Verified |
| :--- | :---: | :--- |
| `SameerMartFlowTest` | 6 | Full lifecycle simulation: Buyer & Seller registration &rarr; Authentication &rarr; Seller adds product &rarr; Buyer browses catalog &rarr; Adds to cart &rarr; Checks out order &rarr; Inventory atomically decremented &rarr; Seller verifies order in dashboard. |

---

## 4. Security Penetration Self-Audit (T3.3)

A rigorous self-penetration audit was conducted across the top web application vulnerability vectors (OWASP Top 10):

```
+----------------------------------------------------------------------------------------------------+
|                                    SECURITY PENETRATION SELF-AUDIT                                 |
+--------------------------+------------------------------------------------+------------------------+
| Vulnerability Vector     | Defense Implementation                         | Audit / Verification   |
+--------------------------+------------------------------------------------+------------------------+
| 1. SQL Injection (SQLi)  | 100% PreparedStatement with parameterized SQL  | SecurityTest           |
|                          | Zero dynamic string concatenation in DAOs      | Manual probe pass      |
+--------------------------+------------------------------------------------+------------------------+
| 2. Cross-Site Scripting  | Context-sensitive escaping via HtmlUtil.escape | SecurityTest           |
|    (XSS)                 | Strict JSP taglib and HTML attribute escaping  | Manual script probes   |
+--------------------------+------------------------------------------------+------------------------+
| 3. Cross-Site Request    | Cryptographic 32-byte SecureRandom tokens      | SecurityTest           |
|    Forgery (CSRF)        | Constant-time comparison (MessageDigest)       | All POST servlets test |
+--------------------------+------------------------------------------------+------------------------+
| 4. Broken Auth & Session | Session fixation defense (changeSessionId)     | LoginServletTest       |
|    Management            | BCrypt cost factor 12 (60 chars)               | AuthFilterTest         |
+--------------------------+------------------------------------------------+------------------------+
| 5. Brute Force Attacks   | Sliding-window lockout (5 fails / 5 min)       | LoginServletTest       |
|                          | Protects BCrypt & DB from CPU starvation       | ChatServiceTest        |
+--------------------------+------------------------------------------------+------------------------+
| 6. Authorization (BOLA / | Server-side role filter + DAO-scoped ownership | AuthFilterTest         |
|    BFLA / IDOR)          | Buyers/Sellers cannot access others' resources | Servlet unit tests     |
+--------------------------+------------------------------------------------+------------------------+
| 7. Path Traversal & File | UUID renaming, MIME whitelist (JPEG/PNG/WEBP), | AddProductServletTest  |
|    Upload Abuse          | Quarantined external upload directory          | EditProductServletTest |
+--------------------------+------------------------------------------------+------------------------+
```

### 4.1 SQL Injection (SQLi) Audit
- **Vulnerability**: Attackers supply SQL syntax inside user input fields to alter query semantics or bypass authentication.
- **Defense Mechanism**:
  1. Every single database query across `UserDAO`, `ProductDAO`, `CartDAO`, `OrderDAO`, `ReviewDAO`, and `WishlistDAO` strictly utilizes `java.sql.PreparedStatement` with typed parameter placeholders (`?`).
  2. All JDBC operations use try-with-resources blocks ensuring connection and statement release.
  3. No `Statement.createStatement()` or dynamic string concatenation exists in application code.
- **Automated Probes (`SecurityTest.testSqlInjectionPayloadsFailInUserLookup`)**:
  - `' OR '1'='1`
  - `' OR 1=1 --`
  - `admin' --`
  - `' UNION SELECT id, name, email, password, role FROM users --`
- **Result**: **PASS** — Every payload is treated strictly as an inert string literal. Query returns zero unauthorized rows.

### 4.2 Cross-Site Scripting (XSS) Audit
- **Vulnerability**: Malicious scripts injected via user-submitted fields (e.g. product titles, descriptions, customer reviews, search queries) executing in other users' browsers.
- **Defense Mechanism**:
  1. Central utility `HtmlUtil.escape(String)` converts all hazardous characters to safe HTML entities:
     - `&` &rarr; `&amp;`
     - `<` &rarr; `&lt;`
     - `>` &rarr; `&gt;`
     - `"` &rarr; `&quot;`
     - `'` &rarr; `&#x27;`
     - `/` &rarr; `&#x2F;`
  2. All JSP presentation pages (`marketplace.jsp`, `product-details.jsp`, `cart.jsp`, `orders.jsp`, `buyer/wishlist.jsp`, `seller/dashboard.jsp`, `admin/dashboard.jsp`, `login.jsp`, `register.jsp`) run untrusted text through `HtmlUtil.escape()`.
- **Automated Probes (`SecurityTest.testHtmlUtilEscapesXssPayloads`)**:
  - `<script>alert('XSS')</script>` &rarr; rendered as `&lt;script&gt;alert(&#x27;XSS&#x27;)&lt;/script&gt;`
  - `<img src=x onerror=alert(1)>` &rarr; rendered as `&lt;img src=x onerror=alert(1)&gt;`
  - `" onmouseover="alert('xss')" & '` &rarr; rendered as `&quot; onmouseover=&quot;alert(&#x27;xss&#x27;)&quot; &amp; &#x27;`
- **Result**: **PASS** — Zero browser script execution possible.

### 4.3 Cross-Site Request Forgery (CSRF) Audit
- **Vulnerability**: Malicious third-party sites issuing unauthorized state-changing POST requests using a victim's active session cookie.
- **Defense Mechanism**:
  1. Synchronizer Token Pattern implemented in `CsrfUtil`.
  2. Per-session cryptographically random 32-byte tokens generated via `java.security.SecureRandom`, encoded in URL-safe Base64 without padding.
  3. Stored in `session.getAttribute("csrfToken")` and included in every state-changing form and AJAX request.
  4. Validated on every state-changing POST using `CsrfUtil.constantTimeEquals()` via `MessageDigest.isEqual()`, mitigating timing side-channel attacks.
- **Automated Tests (`SecurityTest.testCsrfConstantTimeComparison`, `SecurityTest.testCsrfValidationWorkflow`, `SecurityTest.testCsrfTokenUniquenessAndEntropy`)**:
  - Missing token &rarr; rejected (false)
  - Mismatched token &rarr; rejected (false)
  - Null session &rarr; rejected (false)
  - Valid token &rarr; accepted (true)
  - Timing attack resilience &rarr; constant-time byte equality
- **Result**: **PASS** — State-changing endpoints cannot be forged.

### 4.4 Authentication & Session Management Audit
- **Vulnerability**: Credential theft, weak password hashes, session fixation.
- **Defense Mechanism**:
  1. Passwords hashed using **jBCrypt** with cost factor 12 (Salted Blowfish, 60 characters). Verified in `SecurityTest.testBcryptHashStrength()`.
  2. Plaintext passwords never persisted or logged.
  3. Session fixation defense: `request.changeSessionId()` called immediately upon successful authentication in `LoginServlet` to regenerate the session ID.
- **Result**: **PASS**.

### 4.5 Brute Force & Credential Stuffing Defense Audit
- **Vulnerability**: Automated attackers attempting thousands of password guesses against user accounts, causing account takeover or CPU starvation from BCrypt calculations.
- **Defense Mechanism**:
  1. `LoginServlet` maintains a concurrent in-memory sliding-window failed attempt tracker keyed by `clientIp + ":" + email`.
  2. Maximum 5 failed attempts allowed within a 5-minute sliding window (`LOCKOUT_DURATION_MS = 300,000 ms`).
  3. On the 6th failed attempt, the request is immediately short-circuited and redirected with `Too many failed login attempts. Please try again in 5 minutes.`
  4. Crucially, the rate limiter check runs **before** `UserDAO.findByEmail()` or `UserDAO.verifyPassword()`, completely insulating the database and BCrypt hashing engine from denial-of-service starvation.
  5. Upon any successful authentication, the failed attempt counter is cleared immediately.
- **Automated Tests (`LoginServletTest.testLoginRateLimitingAfterExcessiveFailures`, `LoginServletTest.testSuccessfulLoginResetsFailedAttemptCounter`)**:
  - 5 failed attempts record failures.
  - 6th attempt is blocked without querying `UserDAO`.
  - Counter reset verified upon correct password login.
- **Result**: **PASS**.

### 4.6 Authorization & Access Control (BOLA / BFLA) Audit
- **Vulnerability**: Broken Object Level Authorization (IDOR) where users access or mutate other users' orders/products, and Broken Function Level Authorization where buyers access seller/admin dashboards.
- **Defense Mechanism**:
  1. Server-side role enforcement via `AuthFilter` intercepts `/buyer/*`, `/seller/*`, `/admin/*` routes before servlets execute.
  2. Database-level ownership scoping:
     - Sellers editing or deleting products must match `seller_id = ?` (`ProductDAO.update()`, `ProductDAO.delete()`). Competitors receive unauthorized rejection.
     - Buyers viewing orders can only query their own orders (`OrderDAO.findByUserId()`).
     - Product reviews can only be submitted if the buyer has a verified `DELIVERED` order containing that product (`ReviewDAO.hasPurchasedProduct()`).
- **Automated Tests (`AuthFilterTest`, `EditProductServletTest`, `DeleteProductServletTest`, `OrderServletTest`, `ReviewServletTest`)**:
  - Role-boundary cross-access denied with HTTP 403.
  - Cross-tenant data mutations rejected.
- **Result**: **PASS**.

---

## 5. Static Code Analysis Audit (T2.4)

Static code analysis was integrated into the Maven lifecycle and verified locally and in GitHub Actions CI:

### 5.1 Checkstyle (`maven-checkstyle-plugin:3.4.0`)
- **Ruleset**: `checkstyle.xml` enforcing strict code standards:
  - No wildcard imports (`AvoidStarImport`)
  - No unused imports (`UnusedImports`)
  - Strict naming conventions (camelCase variables, UPPER_SNAKE constants)
  - Mandatory braces on control blocks (`NeedBraces`)
  - Redundant modifiers removal (`RedundantModifier`)
- **Audit Result**: **0 Checkstyle violations**.

### 5.2 SpotBugs (`spotbugs-maven-plugin:4.8.6.4`)
- **Ruleset**: Bytecode-level analysis configured in `pom.xml` with `spotbugs-exclude.xml`.
- **Exclusions**: Tuned for Java EE standard patterns (servlet serialization attributes, DTO field representations).
- **Audit Result**: **0 bugs, 0 errors**.

---

## 6. Distinction Features Verification (Tier 3)

### 6.1 Option O1: Buyer Wishlist (T3.1)
- **Database Schema**: `wishlist` table created with compound primary key `(user_id, product_id)` and foreign keys referencing `users(id)` and `products(id)` with `ON DELETE CASCADE`.
- **Components**:
  - `WishlistItem.java`: Domain model with product metadata (name, price, image, category, availability).
  - `WishlistDAO.java`: SQL queries for wishlist mutations (`addItem`, `removeItem`, `getWishlistByUserId`, `isInWishlist`, `getWishlistCount`).
  - `WishlistService.java` & `WishlistServiceImpl.java`: Service layer abstraction with validation.
  - `WishlistServlet.java`: Controller handling `/buyer/wishlist` actions (`add`, `remove`, `view`).
  - `src/main/webapp/buyer/wishlist.jsp`: Responsive UI with Neo-Brutalist design, stock status badges, "Move to Cart", and "Remove" actions.
- **Verification**: **22 dedicated automated tests** across `WishlistDAOTest` (6), `WishlistServiceTest` (9), and `WishlistServletTest` (7).

### 6.2 Option O3: Seller Sales Intelligence Dashboard (T3.2)
- **Components**:
  - `TopSellingProduct.java`: Metric DTO capturing product ID, name, units sold, total revenue, and stock remaining.
  - `OrderDAO.java` Queries:
    - `getSellerUnitsSold(int sellerId)`: Total units sold across all seller products.
    - `getSellerOrdersByStatus(int sellerId)`: Order distribution mapping (`PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`).
    - `getTopSellingProducts(int sellerId, int limit)`: Ranked leaderboard of top-selling items.
    - `getSellerSalesByCategory(int sellerId)`: Revenue aggregation by product category.
  - `src/main/webapp/seller/dashboard.jsp`:
    - Metric summary cards (Total Revenue, Orders, Products, Units Sold).
    - Chart.js Doughnut Chart: Real-time order fulfillment status breakdown.
    - Chart.js Horizontal Bar Chart: Top 5 products by units sold.
    - Top-Selling Products Leaderboard Table.
- **Verification**: **4 dedicated automated tests** in `OrderDAOTest` verifying metrics aggregation, status counting, and leaderboard ordering.

---

## 7. Real-World High-Concurrency Load Test

A load test was executed against a live Apache Tomcat 9 deployment of SameerMart using the JDK HTTP client load harness (`LoadTestRunner.java`):

```text
LOAD START: users=10 durationMs=60000 endpoint=http://localhost:8080/SameerMart/buyer/marketplace
LOAD DONE:  wallSeconds=60
requests        = 196804
requestsPerSecond = 3280.2
errors          = 0
avgLatencyMs    = 4
minLatencyMs    = 1
maxLatencyMs    = 389
LOAD TEST EXIT CODE: 0
```

- **Sustained Throughput**: **3,280.2 requests/second**
- **Error Rate**: **0.00% (0 errors out of 196,804 requests)**
- **Average Latency**: **4 milliseconds**
- **Concurrency**: **10 parallel virtual clients** continuously requesting dynamic JSP-rendered marketplace catalogs.

---

## 8. Continuous Integration Pipeline

GitHub Actions CI is defined in `.github/workflows/build.yml`:
- Triggered on push and pull requests to `main`.
- Environment: Ubuntu Latest, Eclipse Temurin JDK 17.
- Build Steps:
  1. `actions/checkout@v4`
  2. `actions/setup-java@v4` (JDK 17)
  3. `mvn -B clean verify` (compiles code, runs Checkstyle audit, executes 226 tests, runs SpotBugs audit, packages WAR)
  4. `actions/upload-artifact@v4` (publishes `SameerMart.war`)