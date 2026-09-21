-- SameerMart Seed Data
-- Amounts are stored in integer minor units (paise).

-- Resynchronize identity columns with existing table data
ALTER TABLE users ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM users);
ALTER TABLE products ALTER COLUMN id RESTART WITH (SELECT COALESCE(MAX(id), 0) + 1 FROM products);

-- Seed verified merchant and buyer accounts
INSERT INTO users (name, email, password_hash, role)
SELECT 'Sameer Seller', 'seller@sameermart.com', '$2a$10$PFR/Qxg7ZIKpPb.iVEZU2uMFyWun1jBXrDtPpDNOZMGzu04RJGmxi', 'SELLER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'seller@sameermart.com');

INSERT INTO users (name, email, password_hash, role)
SELECT 'Sameer Buyer', 'buyer@sameermart.com', '$2a$10$belYaSNklChdQdA7WRjDs.aNwYyKZvq3q3l4B.d/tQrff9E9IGpwG', 'BUYER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'buyer@sameermart.com');

-- Seed marketplace catalog across categories
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Wireless Noise-Canceling Headphones',
       'Premium over-ear Bluetooth headphones with active noise cancellation, deep bass, and up to 30 hours of battery life.',
       499900, 25, 'Electronics',
       'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Wireless Noise-Canceling Headphones');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Smart Fitness Watch',
       'Waterproof fitness tracker with heart rate monitor, SpO2 sensor, sleep tracking, and a vibrant AMOLED display.',
       299900, 40, 'Electronics',
       'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Smart Fitness Watch');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Ultra-Slim 27-inch 4K Monitor',
       'IPS LED display with HDR400, 99% sRGB color gamut, USB-C connectivity, and ultra-narrow bezels.',
       1899900, 15, 'Electronics',
       'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Ultra-Slim 27-inch 4K Monitor');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Classic Denim Jacket',
       'Timeless trucker jacket crafted from 100% organic cotton denim with brass buttons and adjustable waist tabs.',
       249900, 30, 'Fashion',
       'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Classic Denim Jacket');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Breathable Running Shoes',
       'Lightweight athletic sneakers featuring responsive foam cushioning and breathable engineered mesh upper.',
       349900, 20, 'Fashion',
       'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Breathable Running Shoes');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Stainless Steel Insulated Bottle',
       'Double-wall vacuum insulated water bottle (750ml) that keeps drinks cold for 24 hours or piping hot for 12 hours.',
       79900, 60, 'Essentials',
       'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Stainless Steel Insulated Bottle');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Minimalist Leather Wallet',
       'Slim bifold wallet made from genuine top-grain leather with RFID-blocking technology and 8 card slots.',
       129900, 45, 'Essentials',
       'https://images.unsplash.com/photo-1627123424574-724758594e93?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Minimalist Leather Wallet');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Compact Espresso Coffee Machine',
       '15-bar Italian pump espresso maker with stainless steel steam wand for rich crema and silky latte froth.',
       899900, 12, 'Home Appliances',
       'https://images.unsplash.com/photo-1517668808822-9ebb02f2a0e6?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Compact Espresso Coffee Machine');

INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url)
SELECT (SELECT id FROM users WHERE role = 'SELLER' ORDER BY id ASC LIMIT 1),
       'Smart Robotic Vacuum Cleaner',
       'Automatic robotic vacuum with precision LiDAR navigation, strong suction, self-charging, and app control.',
       1499900, 8, 'Home Appliances',
       'https://images.unsplash.com/photo-1589779257635-43093cb49a46?w=600&auto=format&fit=crop&q=80'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Smart Robotic Vacuum Cleaner');
