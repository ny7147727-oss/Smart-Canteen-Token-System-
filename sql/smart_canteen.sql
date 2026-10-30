-- ========================================================
-- SmartServe - Database Schema & Sample Data
-- Database: smart_canteen
-- Character Set: utf8mb4
-- Storage Engine: InnoDB
-- ========================================================

CREATE DATABASE IF NOT EXISTS smart_canteen
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE smart_canteen;

-- --------------------------------------------------------
-- Drop existing tables in reverse foreign key order
-- --------------------------------------------------------
DROP TABLE IF EXISTS tokens;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS users;

-- --------------------------------------------------------
-- Table: users
-- --------------------------------------------------------
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(15),
    roll_number VARCHAR(20) UNIQUE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: menu_items
-- --------------------------------------------------------
CREATE TABLE menu_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: orders
-- --------------------------------------------------------
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token_number INT UNIQUE NOT NULL,
    status ENUM('PLACED', 'PREPARING', 'READY', 'COMPLETED', 'CANCELLED') DEFAULT 'PLACED',
    total_amount DECIMAL(10, 2) NOT NULL,
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_time TIMESTAMP NULL,
    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    INDEX idx_orders_user_id (user_id),
    INDEX idx_orders_token (token_number),
    INDEX idx_orders_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: order_items
-- --------------------------------------------------------
CREATE TABLE order_items (
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE,
    FOREIGN KEY (item_id)
        REFERENCES menu_items(item_id)
        ON DELETE RESTRICT,
    INDEX idx_order_items_order (order_id),
    INDEX idx_order_items_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: tokens
-- --------------------------------------------------------
CREATE TABLE tokens (
    token_id INT PRIMARY KEY AUTO_INCREMENT,
    token_number INT UNIQUE NOT NULL,
    order_id INT NOT NULL,
    status ENUM('WAITING', 'CALLED', 'SERVED') DEFAULT 'WAITING',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE CASCADE,
    INDEX idx_tokens_number (token_number),
    INDEX idx_tokens_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Table: payments
-- --------------------------------------------------------
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    order_id INT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('UPI', 'CARD', 'WALLET') NOT NULL,
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    transaction_reference VARCHAR(64) UNIQUE NOT NULL,
    masked_details VARCHAR(100),
    paid_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,
    FOREIGN KEY (order_id)
        REFERENCES orders(order_id)
        ON DELETE SET NULL,
    INDEX idx_payments_user_id (user_id),
    INDEX idx_payments_order_id (order_id),
    INDEX idx_payments_status (payment_status),
    INDEX idx_payments_txn_ref (transaction_reference)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Sample Data: Users
-- --------------------------------------------------------
INSERT INTO users (username, password, role, full_name, email, phone, roll_number) VALUES
('admin', 'admin123', 'ADMIN', 'Admin', 'admin@canteen.edu', '9876543210', NULL),
('rahul', 'rahul123', 'STUDENT', 'Rahul', 'rahul@student.edu', '9876543211', 'CS2023001');

-- --------------------------------------------------------
-- Sample Data: Menu Items (Comprehensive Indian College Canteen Catalog)
-- --------------------------------------------------------
INSERT INTO menu_items (item_name, category, price, available, description) VALUES
-- CATEGORY 1: NORTH INDIAN MEALS
('Chole Bhature', 'North Indian Meals', 80.00, TRUE, 'Two fluffy fried bhaturas served with spicy Punjabi chole, pickle and onion'),
('Rajma Chawal', 'North Indian Meals', 75.00, TRUE, 'Classic slow-cooked kidney beans in rich spiced tomato gravy served with steamed basmati rice'),
('Chole Chawal', 'North Indian Meals', 70.00, TRUE, 'Aromatic spiced chickpea curry served over hot steamed jeera rice with salad'),
('Kadhi Chawal', 'North Indian Meals', 70.00, TRUE, 'Traditional Punjabi tangy yogurt kadhi with soft pakodas served with steamed rice'),
('Dal Tadka with Rice', 'North Indian Meals', 75.00, TRUE, 'Yellow lentils tempered with cumin, garlic, and ghee served with steamed rice'),
('Dal Makhani with Rice', 'North Indian Meals', 90.00, TRUE, 'Creamy slow-cooked black lentils simmered with butter and spices served with rice'),
('Paneer Butter Masala with Roti', 'North Indian Meals', 110.00, TRUE, 'Cottage cheese cubes in rich buttery tomato gravy served with 3 soft butter rotis'),
('Shahi Paneer with Roti', 'North Indian Meals', 115.00, TRUE, 'Royal Mughlai style paneer in creamy cashew-nut gravy served with 3 tawa rotis'),
('Kadhai Paneer with Roti', 'North Indian Meals', 110.00, TRUE, 'Paneer tossed with bell peppers, onions, and freshly ground kadhai masala with 3 rotis'),
('Mix Veg with Roti', 'North Indian Meals', 85.00, TRUE, 'Seasonal fresh vegetables cooked in homestyle spiced gravy served with 3 rotis'),
('Aloo Paratha', 'North Indian Meals', 50.00, TRUE, 'Whole wheat tawa paratha stuffed with spiced mashed potatoes, served with pickle'),
('Paneer Paratha', 'North Indian Meals', 70.00, TRUE, 'Wholesome wheat paratha stuffed with seasoned crumbled cottage cheese'),
('Gobi Paratha', 'North Indian Meals', 55.00, TRUE, 'Crispy golden paratha stuffed with spiced grated cauliflower and herbs'),
('Stuffed Paratha Combo', 'North Indian Meals', 95.00, TRUE, 'Two assorted stuffed parathas (Aloo & Paneer) served with curd and mint chutney'),
('Jeera Rice', 'North Indian Meals', 60.00, TRUE, 'Fluffy basmati rice tempered with roasted cumin seeds and fresh coriander'),
('Veg Pulao', 'North Indian Meals', 80.00, TRUE, 'Fragrant rice cooked with garden green peas, carrots, beans, and whole spices'),
('Veg Thali', 'North Indian Meals', 100.00, TRUE, 'Complete meal: Dal, seasonal sabzi, 3 rotis, rice, salad, and pickle'),
('Special North Indian Thali', 'North Indian Meals', 140.00, TRUE, 'Paneer Butter Masala, Dal Makhani, Mix Veg, Jeera Rice, 3 Butter Rotis, Gulab Jamun, and Salad'),

-- CATEGORY 2: CHAAT
('Pani Puri', 'Chaat', 35.00, TRUE, '8 crispy puris filled with spiced potato-sprouts mash, sweet tamarind chutney, and spicy mint water'),
('Aloo Tikki Chaat', 'Chaat', 50.00, TRUE, 'Two crispy golden potato patties topped with warm chole, sweetened curd, and tangy chutneys'),
('Papdi Chaat', 'Chaat', 50.00, TRUE, 'Crispy flour crisps layered with boiled potatoes, chickpeas, whipped yogurt, and spices'),
('Dahi Puri', 'Chaat', 55.00, TRUE, 'Crispy puris stuffed with potato mash, chilled sweetened curd, sev, and sweet-sour chutneys'),
('Bhel Puri', 'Chaat', 40.00, TRUE, 'Puffed rice tossed with onions, tomatoes, raw mango, chutneys, and crisp sev'),
('Sev Puri', 'Chaat', 45.00, TRUE, 'Flat crisp papdis topped with spiced potatoes, onions, trio of chutneys, and nylon sev'),
('Raj Kachori', 'Chaat', 75.00, TRUE, 'King-sized crisp kachori shell stuffed with bhallas, sprouts, yogurt, chutneys, and pomegranate'),
('Dahi Bhalla', 'Chaat', 55.00, TRUE, 'Soft melt-in-mouth lentil dumplings immersed in spiced curd topped with roasted cumin and saunth'),
('Samosa Chaat', 'Chaat', 50.00, TRUE, 'Crushed hot samosa drenched with Punjabi chole curry, whisked yogurt, and coriander chutney'),
('Chole Tikki Chaat', 'Chaat', 60.00, TRUE, 'Crispy spiced aloo tikki drenched in rich Amritsari chole gravy with diced ginger and onions'),

-- CATEGORY 3: HEALTHY FOOD
('Sprouts Chaat', 'Healthy Food', 50.00, TRUE, 'Protein-packed steamed moong and chana sprouts tossed with lemon juice, cucumber, and chaat masala'),
('Fresh Fruit Bowl', 'Healthy Food', 60.00, TRUE, 'Freshly sliced seasonal fruits including apple, papaya, watermelon, pineapple, and pomegranate'),
('Mixed Fruit Chaat', 'Healthy Food', 65.00, TRUE, 'Chilled seasonal fruits tossed with black salt, roasted cumin, and a splash of fresh lemon'),
('Vegetable Salad', 'Healthy Food', 45.00, TRUE, 'Crunchy slices of cucumber, tomato, carrot, beetroot, and onion with lemon wedge'),
('Paneer Salad', 'Healthy Food', 80.00, TRUE, 'Fresh cottage cheese cubes tossed with bell peppers, cucumbers, lettuce, and herb lemon dressing'),
('Grilled Vegetable Sandwich', 'Healthy Food', 55.00, TRUE, 'Whole wheat bread grilled with zucchini, capsicum, tomato, and mint yogurt spread'),
('Grilled Paneer Sandwich', 'Healthy Food', 70.00, TRUE, 'Golden grilled multigrain sandwich stuffed with spiced cottage cheese and crunch veggies'),
('Vegetable Wrap', 'Healthy Food', 60.00, TRUE, 'Soft whole wheat tortilla rolled with spiced sautéed veggies, shredded lettuce, and hung curd spread'),
('Paneer Wrap', 'Healthy Food', 80.00, TRUE, 'Whole wheat wrap stuffed with grilled marinated paneer tikka strips and crisp bell peppers'),
('Oats Bowl', 'Healthy Food', 50.00, TRUE, 'Warm nutritious rolled oats cooked in milk with honey, sliced bananas, and chia seeds'),
('Yogurt and Fruit Bowl', 'Healthy Food', 65.00, TRUE, 'Probiotic Greek-style curd topped with honey, fresh seasonal berries, and fruit chunks'),
('Corn and Vegetable Bowl', 'Healthy Food', 55.00, TRUE, 'Steamed sweet corn kernels tossed with steamed veggies, herbs, butter, and pepper'),

-- CATEGORY 4: BREAKFAST
('Poha', 'Breakfast', 35.00, TRUE, 'Light and fluffy flattened rice tempered with mustard seeds, curry leaves, onions, and crunchy peanuts'),
('Upma', 'Breakfast', 35.00, TRUE, 'Traditional savory semolina dish tempered with ginger, mustard seeds, and garden vegetables'),
('Aloo Paratha (Breakfast)', 'Breakfast', 50.00, TRUE, 'Hot tawa paratha stuffed with seasoned potato mash served with curd and butter'),
('Paneer Paratha (Breakfast)', 'Breakfast', 70.00, TRUE, 'Protein-rich tawa paratha stuffed with spiced grated paneer and herbs'),
('Vegetable Sandwich', 'Breakfast', 40.00, TRUE, 'Classic sandwich layered with cucumber, tomato, boiled potato, and green chutney'),
('Idli Sambar', 'Breakfast', 35.00, TRUE, 'Two steamed fluffy rice cakes served with hot vegetable sambar and fresh coconut chutney'),
('Masala Dosa', 'Breakfast', 55.00, TRUE, 'Crispy golden fermented crepe stuffed with spiced potato masala, served with sambar and chutney'),
('Besan Chilla', 'Breakfast', 45.00, TRUE, 'Two savory gram flour pancakes loaded with finely chopped onions, tomatoes, and green chilies'),
('Moong Dal Chilla', 'Breakfast', 50.00, TRUE, 'Healthy and protein-rich yellow lentil crepes served with mint and tamarind chutneys'),

-- CATEGORY 5: SNACKS
('Samosa', 'Snacks', 20.00, TRUE, 'Two crispy golden triangular pastries filled with spiced potatoes, peas, and coriander'),
('Paneer Samosa', 'Snacks', 30.00, TRUE, 'Crispy puff pastry stuffed with spicy crumbled paneer and herbs'),
('Veg Cutlet', 'Snacks', 35.00, TRUE, 'Two crispy crumb-coated vegetable patties packed with beetroot, peas, and potatoes'),
('Bread Pakora', 'Snacks', 25.00, TRUE, 'Spiced potato sandwich coated in seasoned gram flour batter and fried till golden'),
('Kachori', 'Snacks', 25.00, TRUE, 'Flaky crispy pastry stuffed with spiced moong dal filling served with tangy saunth'),
('Dhokla', 'Snacks', 35.00, TRUE, 'Steamed spongy fermented gram flour cakes tempered with mustard seeds and green chilies'),
('Paneer Tikka', 'Snacks', 90.00, TRUE, 'Skewered cubes of cottage cheese and capsicum marinated in spiced yogurt and grilled'),
('French Fries', 'Snacks', 45.00, TRUE, 'Crispy golden potato fingers served hot with tomato ketchup'),
('Masala Fries', 'Snacks', 55.00, TRUE, 'Crispy potato fries dusted with zesty peri-peri and chaat masala seasoning'),
('Veg Spring Roll', 'Snacks', 60.00, TRUE, 'Crispy rolled wrappers stuffed with seasoned shredded cabbage, carrots, and glass noodles'),

-- CATEGORY 6: BEVERAGES
('Masala Chaas', 'Beverages', 25.00, TRUE, 'Refreshing spiced buttermilk infused with roasted cumin, ginger, and fresh mint leaves'),
('Sweet Lassi', 'Beverages', 40.00, TRUE, 'Thick creamy Punjabi yogurt drink churned with sugar and topped with malai'),
('Mango Lassi', 'Beverages', 50.00, TRUE, 'Delicious smooth blend of sweet Alphonso mango pulp and chilled thick yogurt'),
('Fresh Lemon Water', 'Beverages', 20.00, TRUE, 'Chilled lemonade with a balanced sweet and salted touch'),
('Mint Lemon Water', 'Beverages', 25.00, TRUE, 'Icy lemon cooler blended with crushed mint leaves and black salt'),
('Fresh Orange Juice', 'Beverages', 50.00, TRUE, 'Freshly squeezed pure orange juice without added preservatives'),
('Watermelon Juice', 'Beverages', 40.00, TRUE, 'Hydrating fresh watermelon juice served chilled with a hint of rock salt'),
('Coconut Water', 'Beverages', 45.00, TRUE, 'Natural pure tender coconut water served chilled'),
('Cold Coffee', 'Beverages', 50.00, TRUE, 'Creamy blended iced coffee made with rich espresso, cold milk, and chocolate drizzle'),
('Masala Tea', 'Beverages', 15.00, TRUE, 'Hot freshly brewed milk tea simmered with fresh crushed ginger and cardamom'),
('Green Tea', 'Beverages', 20.00, TRUE, 'Soothing hot infusion of whole organic green tea leaves'),

-- CATEGORY 7: HEALTHY BEVERAGES
('Detox Lemon Mint', 'Healthy Beverages', 35.00, TRUE, 'Infused detox water with cucumber, fresh lemon slices, and crushed mint sprigs'),
('Fruit Smoothie', 'Healthy Beverages', 65.00, TRUE, 'Nutrient-dense thick smoothie blended with mixed berries, banana, and Greek yogurt'),
('Banana Smoothie', 'Healthy Beverages', 50.00, TRUE, 'Energizing thick shake made with ripe bananas, honey, milk, and crushed almonds'),
('Mango Smoothie', 'Healthy Beverages', 65.00, TRUE, 'Creamy tropical smoothie made with real mango chunks and probiotic yogurt'),
('Buttermilk', 'Healthy Beverages', 25.00, TRUE, 'Traditional low-fat salted buttermilk tempered with curry leaves and hing'),
('Fresh Coconut Water', 'Healthy Beverages', 45.00, TRUE, 'Naturally electrolyte-rich fresh tender coconut water served directly'),

-- CATEGORY 8: COMBOS
('Chole Bhature + Chaas', 'Combos', 95.00, TRUE, 'Two hot bhaturas with rich Punjabi chole served with a glass of spiced masala chaas'),
('Rajma Chawal + Salad', 'Combos', 90.00, TRUE, 'Generous portion of Rajma Chawal served with fresh garden green salad and pickle'),
('Veg Thali + Chaas', 'Combos', 120.00, TRUE, 'Complete North Indian Thali served along with a refreshing glass of chilled masala chaas'),
('Aloo Paratha + Curd', 'Combos', 65.00, TRUE, 'Two tawa aloo parathas served with a bowl of fresh chilled curd and mixed pickle'),
('Paneer Paratha + Lassi', 'Combos', 105.00, TRUE, 'Two hearty stuffed paneer parathas accompanied with a tall glass of sweet Punjabi lassi'),
('Samosa + Masala Tea', 'Combos', 30.00, TRUE, 'Classic student favorite: Two hot crispy samosas with a steaming cup of adrak masala chai'),
('Aloo Tikki + Lemon Water', 'Combos', 65.00, TRUE, 'Crispy Aloo Tikki Chaat paired with a chilled glass of fresh mint lemon water'),
('Grilled Sandwich + Fresh Juice', 'Combos', 90.00, TRUE, 'Grilled vegetable sandwich paired with fresh watermelon or orange juice'),
('Healthy Salad + Lemon Water', 'Combos', 60.00, TRUE, 'Nutritious sprouts or vegetable salad served with chilled detox lemon water'),
('Paneer Wrap + Smoothie', 'Combos', 135.00, TRUE, 'Grilled marinated paneer tikka wrap served with a rich banana or fruit smoothie');

