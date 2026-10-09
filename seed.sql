-- ====================================================================
-- SameerMart: Realistic Production Seed Data
-- ====================================================================

-- 1. SEED USERS
-- Passwords hashed with jBCrypt (12 rounds)
-- admin@sameermart.com -> admin123
-- seller@sameermart.com -> seller123
-- buyer@sameermart.com  -> buyer123
INSERT INTO users (id, name, email, password, role) VALUES
(1, 'Platform Administrator', 'admin@sameermart.com', '$2a$12$g.cU5i0d4yhzmMNCeE6lkuh4nFj8PJPT0WrlZ9N6FMMRNxW3dMV/S', 'ADMIN'),
(2, 'Mohamed Agro Farms', 'seller@sameermart.com', '$2a$12$AEALBAJnKkIn1SJkOnKYn.HdioYdIaim23BtZicZq6AOL4ANM9QF6', 'SELLER'),
(3, 'Aisha Rahman', 'buyer@sameermart.com', '$2a$12$qU/2VE4Sa5zRAspzexNsWOS69hfMWRv9TOHk1bG21gp5J7k8k0I.i', 'BUYER');

-- 2. SEED PRODUCTS (Across Groceries, Electronics, Fashion, Books)
INSERT INTO products (id, seller_id, name, description, category, price, quantity, image_url, active) VALUES
(1, 2, 'Organic Wild Honey 500g', 'Pure raw forest honey sourced directly from sustainable tribal apiaries.', 'Groceries', 450.00, 35, 'default-product.svg', TRUE),
(2, 2, 'Cold Pressed Coconut Oil 1L', 'Traditional wood-pressed extra virgin coconut oil for cooking and wellness.', 'Groceries', 320.00, 50, 'default-product.svg', TRUE),
(3, 2, 'Noise Cancelling Wireless Earbuds', 'Bluetooth 5.3 active noise-cancelling earbuds with 32h playback time.', 'Electronics', 2499.00, 20, 'default-product.svg', TRUE),
(4, 2, 'USB-C 65W Fast Charger', 'GaN fast charger adapter with dual Type-C and USB-A power delivery.', 'Electronics', 1299.00, 40, 'default-product.svg', TRUE),
(5, 2, 'Pure Cotton Casual Shirt (Blue)', 'Breathable organic cotton slim-fit shirt with mother-of-pearl buttons.', 'Fashion', 899.00, 30, 'default-product.svg', TRUE),
(6, 2, 'Linen Comfort Trousers', 'Classic fit lightweight linen blend trousers for warm weather style.', 'Fashion', 1199.00, 25, 'default-product.svg', TRUE),
(7, 2, 'Designing Data-Intensive Applications', 'The definitive guide to distributed systems architecture by Martin Kleppmann.', 'Books', 1450.00, 15, 'default-product.svg', TRUE),
(8, 2, 'Clean Code: Agile Software Craftsmanship', 'A handbook of agile software craftsmanship and best practices by Robert C. Martin.', 'Books', 850.00, 18, 'default-product.svg', TRUE);

-- 3. SEED CART FOR BUYER
INSERT INTO cart (id, buyer_id) VALUES
(1, 3);

INSERT INTO cart_items (id, cart_id, product_id, quantity) VALUES
(1, 1, 1, 2);

-- 4. SEED ORDERS
-- Order 1: DELIVERED (Eligible for reviews)
INSERT INTO orders (id, buyer_id, total_amount, status) VALUES
(1, 3, 1219.00, 'DELIVERED'),
(2, 3, 2499.00, 'SHIPPED');

-- Order 1 Items (Products 5 & 2)
INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
(1, 1, 5, 1, 899.00),
(2, 1, 2, 1, 320.00);

-- Order 2 Items (Product 3)
INSERT INTO order_items (id, order_id, product_id, quantity, price) VALUES
(3, 2, 3, 1, 2499.00);

-- 5. SEED VERIFIED REVIEWS (On delivered products from Order 1)
INSERT INTO reviews (id, product_id, buyer_id, rating, review_text) VALUES
(1, 5, 3, 5, 'Outstanding fabric quality and comfortable fit! Excellent packaging.'),
(2, 2, 3, 4, 'Pure and authentic aroma. Arrived on time and in perfect glass container.');
