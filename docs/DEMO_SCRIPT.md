# SameerMart — 10-Minute Final Review Presentation Script

**Project:** SameerMart Multi-Vendor E-Commerce Platform  
**Target Audience:** Anna University External & Internal Examiners  
**Estimated Time:** 10 Minutes Total (8.5 mins demo + 1.5 mins Q&A buffer)  
**Presenter:** Mohamed Sameer  

---

## 🎯 Pre-Demo Setup Checklist (Complete 5 Minutes Before)

- [ ] **Tomcat running**: `http://localhost:8080/SameerMart` is loaded in your primary browser.
- [ ] **Three browser windows / incognito sessions open**:
  - Window 1: **Admin Session** (Logged in as `admin@sameermart.com`)
  - Window 2: **Seller Session** (Logged in as `seller@sameermart.com`)
  - Window 3: **Buyer Session** (Logged in as `buyer@sameermart.com`)
- [ ] **Terminal open**: Split terminal showing `mvn test` output and active Tomcat / application logs.
- [ ] **Sample product image ready**: A small JPEG image on your desktop (`sample-item.jpg`).

---

## ⏱️ Step-by-Step Presentation Breakdown

### 1. Introduction & Architecture Overview (1.0 Minute)
- **What to Say**:
  > *"Good morning, esteemed examiners. My name is Mohamed Sameer, and this is SameerMart—an enterprise-grade, multi-vendor electronic commerce platform engineered with Java 17, Servlets, JSP, HikariCP, and Google Gemini AI assistance per the Anna University R2025 Semester 3 Capstone Specification."*
- **What to Show**:
  - Open `README.md` or show the **Mermaid Architecture Diagram**:
  > *"The architecture strictly follows the MVC model. Requests flow through an Encoding filter, a correlation Request-Id filter, and an Auth filter. State-changing operations are shielded by per-session cryptographic CSRF tokens. Data persistence supports local H2 and cloud PostgreSQL with HikariCP connection pooling."*

---

### 2. Administrator Dashboard & Moderation (1.5 Minutes)
- **What to Say**:
  > *"First, let us examine the Administrator portal."*
- **What to Show**:
  - Switch to **Window 1 (Admin)**: `http://localhost:8080/SameerMart/admin/dashboard.jsp`
  - Highlight the platform metrics: **Total Gross Merchandise Value (GMV)**, **Total Orders**, and **Catalog Statistics**.
  - Show the **Catalog Moderation Table**:
    - Click **"Deactivate"** on a test product.
    - Explain: *"Administrators can immediately moderate listings. When deactivated, the item is instantaneously hidden from buyers while maintaining historical referential integrity."*
    - Toggle it back to **"Activate"**.

---

### 3. Seller Operations & Inventory Management (2.0 Minutes)
- **What to Say**:
  > *"Next, let's step into the shoes of a registered Seller."*
- **What to Show**:
  - Switch to **Window 2 (Seller)**: `http://localhost:8080/SameerMart/seller/dashboard.jsp`
  - Navigate to **"Add Product"**:
    - Title: `Organic Himalayan Honey`
    - Category: `Groceries`
    - Price: `₹450.00`
    - Stock: `25`
    - Choose file: Upload `sample-item.jpg`
    - Click **"Publish Product"**.
  - Point out:
    > *"Notice that the image was not saved into the transient WAR folder. It was quarantined into an external directory outside web root, assigned a cryptographic UUID filename, and verified to prevent path traversal attacks."*
  - Open **"Customer Orders"** (`/seller/orders`):
    - Highlight seller isolation: *"The seller can only see orders containing their own products; competitor data is completely isolated."*

---

### 4. Buyer Journey: Discovery, Cart & Atomic Checkout (2.5 Minutes)
- **What to Say**:
  > *"Now, let's look at the customer experience."*
- **What to Show**:
  - Switch to **Window 3 (Buyer)**: `http://localhost:8080/SameerMart/buyer/marketplace`
  - **Search & Filter**: Search for `"Honey"`, select category `"Groceries"`. The freshly added item appears immediately.
  - Click on the product to view **Product Details**:
    - Note the stock indicator (`25 available`) and the empty review section.
  - Click **"Add to Cart"** (Quantity: 2).
  - Go to **Cart** (`/buyer/cart`):
    - Update quantity from 2 to 3.
  - Click **"Proceed to Checkout"** & **"Confirm Order"**:
  - **Explain the Atomic Transaction (Crucial Exam Point)**:
    > *"Here, the system executes an atomic transaction in `OrderDAO`: auto-commit is disabled, stock is locked and verified, line items freeze the purchase price snapshot, stock is decremented from 25 down to 22, the cart is cleared, and the transaction commits atomically. If any error had occurred, a full rollback would guarantee zero phantom orders."*
  - The browser redirects to **Order Confirmation**: `Order #X Confirmed`.

---

### 5. Order Lifecycle Progression & Review Gating (1.0 Minute)
- **What to Say**:
  > *"Now let's demonstrate our O2 Order Lifecycle Workflow and the F8 Review constraint."*
- **What to Show**:
  - As Buyer, open the product details:
    - Point out: *"Notice that the review form is disabled or states that reviews require a DELIVERED order."*
  - Switch to **Window 2 (Seller)** & refresh `/seller/orders`:
    - The new order appears with status badge **CONFIRMED**.
    - Click **"Mark Shipped"** &rarr; status updates to **SHIPPED** (purple badge).
    - Click **"Mark Delivered"** &rarr; status updates to **DELIVERED** (green badge).
  - Switch back to **Window 3 (Buyer)** & refresh **"My Orders"**:
    - The order status shows **DELIVERED**.
    - The **"★ Leave Review"** button is now unlocked!
    - Click it, submit a **5-star review** with comment: *"Excellent pure honey, lightning fast delivery!"*.
    - Refresh product details: The average rating computes instantly to `5.0 ★` and the review appears safely sanitized against XSS.

---

### 6. Conversational AI Assistant & Rate Limiting (1.5 Minutes)
- **What to Say**:
  > *"SameerMart features an embedded AI Support Assistant."*
- **What to Show**:
  - On any buyer page, click the floating circular **💬 Chat icon** in the bottom-right corner.
  - Ask an FAQ question:
    - Query: *"What is your return policy?"*
    - Assistant responds immediately with the store policy.
  - Ask a catalog query:
    - Query: *"Can you suggest some groceries?"*
    - Assistant recommends relevant items from the catalog.
  - Show the **Rate Limiter**:
    - Send rapid successive messages to demonstrate sliding window protection (10 req/minute).
    - The assistant returns a polite notification when rate-limited.

---

### 7. Observability & Health Probes (0.5 Minute)
- **What to Say**:
  > *"Finally, let's observe our production telemetry."*
- **What to Show**:
  - Open a new tab: `http://localhost:8080/SameerMart/api/v1/health`
  - Show the standardized JSON envelope:
    ```json
    {
      "success": true,
      "data": {
        "status": "UP",
        "database": "UP",
        "databaseDialect": "H2",
        "poolActiveConnections": 0,
        "poolIdleConnections": 10
      }
    }
    ```
  - Switch to terminal:
    - Highlight the log output displaying correlation tracking:
      `[http-nio-8080-exec-4] [c2a9e3d1-....] INFO c.s.s.servlet.OrderServlet - Order created successfully`

---

## 🛡️ Contingency & Troubleshooting Guide

| Potential Issue | What to Do / What to Say |
| :--- | :--- |
| **No Internet Connection / Wi-Fi Drops** | Calmly explain: *"The entire platform operates 100% offline using local embedded H2. The AI assistant automatically detects offline mode and invokes the `MockChatProvider` rule engine without crashing."* |
| **Gemini API Key Exceeded Quota / 429** | The chat widget will seamlessly return the mock intent match. Explain: *"Our multi-provider fallback architecture caught the upstream rate limit and safely degraded to the fallback engine."* |
| **Port 8080 in use** | In standalone Tomcat, change `conf/server.xml` connector port to `8081` or start Tomcat with `-Dmaven.tomcat.port=8081`. |
| **Examiner asks: "How do you prevent SQL injection?"** | Show `OrderDAO.java` or `ProductDAO.java`: Point out that 100% of user inputs use `PreparedStatement` with parameterized placeholders (`?`) in try-with-resources. |
| **Examiner asks: "How do you prevent CSRF?"** | Show `CsrfUtil.java`: Explain per-session 32-byte cryptographic tokens compared in constant time (`MessageDigest.isEqual`) on all state-changing POST requests. |

---

## 🎓 Wrap-Up & Closing Statement
> *"In summary, SameerMart fulfills 100% of the functional requirements F1 through F8, reinforced with 153 passing automated tests, atomic transaction safety, defense-in-depth security, and generative AI support. Thank you for your time, and I welcome any questions."*
