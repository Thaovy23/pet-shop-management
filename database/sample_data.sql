USE petshop_db;

-- Tắt Safe Update Mode
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- XÓA HẾT DỮ LIỆU
TRUNCATE TABLE bill_items;
TRUNCATE TABLE bills;
TRUNCATE TABLE products;
TRUNCATE TABLE pets;
TRUNCATE TABLE customers;

-- Bật lại
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- ===== CUSTOMERS với số điện thoại đủ 9-10 chữ số =====
INSERT INTO `customers` (`name`, `email`, `phone`, `loyalty_points`) VALUES
('John Smith', 'john.smith@email.com', '555-012-3456', 150),
('Emily Johnson', 'emily.johnson@email.com', '555-023-4567', 200),
('Michael Brown', 'michael.brown@email.com', '555-034-5678', 75),
('Sarah Davis', 'sarah.davis@email.com', '555-045-6789', 300),
('David Wilson', 'david.wilson@email.com', '555-056-7890', 50),
('Lisa Anderson', 'lisa.anderson@email.com', '555-067-8901', 175),
('James Miller', 'james.miller@email.com', '555-078-9012', 125),
('Jessica Taylor', 'jessica.taylor@email.com', '555-089-0123', 250),
('Robert Garcia', 'robert.garcia@email.com', '555-090-1234', 100),
('Amanda Rodriguez', 'amanda.rodriguez@email.com', '555-101-2345', 325);

-- ===== PETS chỉ với DOG và CAT =====
INSERT INTO `pets` (`name`, `type`, `breed`, `age`, `price`, `status`) VALUES
-- Dogs
('Buddy', 'DOG', 'Golden Retriever', 2, 800.00, 1),
('Max', 'DOG', 'German Shepherd', 1, 1200.00, 1),
('Luna', 'DOG', 'Labrador', 3, 750.00, 1),
('Charlie', 'DOG', 'Beagle', 1, 600.00, 1),
('Bella', 'DOG', 'French Bulldog', 2, 1500.00, 1),
('Rocky', 'DOG', 'Rottweiler', 4, 900.00, 1),
('Lucy', 'DOG', 'Poodle', 1, 700.00, 1),
('Duke', 'DOG', 'Boxer', 3, 850.00, 1),
('Molly', 'DOG', 'Border Collie', 2, 950.00, 1),
('Jack', 'DOG', 'Jack Russell Terrier', 1, 550.00, 1),

-- Cats
('Whiskers', 'CAT', 'Persian', 2, 400.00, 1),
('Shadow', 'CAT', 'Maine Coon', 1, 600.00, 1),
('Mittens', 'CAT', 'Siamese', 3, 350.00, 1),
('Tiger', 'CAT', 'Bengal', 2, 800.00, 1),
('Princess', 'CAT', 'British Shorthair', 1, 450.00, 1),
('Smokey', 'CAT', 'Russian Blue', 2, 500.00, 1),
('Ginger', 'CAT', 'Orange Tabby', 1, 250.00, 1),
('Cleo', 'CAT', 'Egyptian Mau', 3, 700.00, 1),
('Felix', 'CAT', 'Scottish Fold', 2, 550.00, 1),
('Nala', 'CAT', 'Ragdoll', 1, 650.00, 1);

-- ===== PRODUCTS với ngày hết hạn xa =====
INSERT INTO `products` (`name`, `price`, `stock_quantity`, `type`, `material`, `expiration_date`, `nutritional_info`, `manufacture_date`, `dosage`, `status`) VALUES
-- FOOD với ngày hết hạn 2027
('Premium Dog Food - Chicken & Rice', 45.99, 50, 'FOOD', NULL, '2027-12-31', 'Protein 26%, Fat 16%, Fiber 4%, Moisture 10%', '2024-06-15', NULL, 1),
('Kitten Formula - Salmon Flavor', 32.50, 35, 'FOOD', NULL, '2027-11-30', 'Protein 32%, Fat 20%, Fiber 3%, Moisture 12%', '2024-04-20', NULL, 1),
('Adult Cat Food - Tuna & Vegetables', 28.75, 40, 'FOOD', NULL, '2027-10-31', 'Protein 28%, Fat 14%, Fiber 3.5%, Moisture 10%', '2024-08-10', NULL, 1),
('Large Breed Puppy Food', 52.00, 25, 'FOOD', NULL, '2027-09-30', 'Protein 28%, Fat 18%, Fiber 4%, Moisture 10%', '2024-07-22', NULL, 1),
('Senior Dog Food - Joint Care', 38.90, 30, 'FOOD', NULL, '2027-08-31', 'Protein 24%, Fat 12%, Fiber 4.5%, Moisture 10%', '2024-05-30', NULL, 1),

-- TOY không có ngày hết hạn
('Rope Toy - Cotton Braided', 8.99, 80, 'TOY', 'Cotton rope', NULL, NULL, NULL, NULL, 1),
('Interactive Puzzle Ball', 15.75, 45, 'TOY', 'Durable plastic', NULL, NULL, NULL, NULL, 1),
('Feather Wand Cat Toy', 12.50, 60, 'TOY', 'Plastic handle, natural feathers', NULL, NULL, NULL, NULL, 1),
('Squeaky Duck Toy', 6.25, 100, 'TOY', 'Rubber', NULL, NULL, NULL, NULL, 1),
('Catnip Mouse Set (3 pieces)', 9.99, 70, 'TOY', 'Fabric, catnip filling', NULL, NULL, NULL, NULL, 1),
('Dental Chew Bone - Large', 11.75, 55, 'TOY', 'Nylon', NULL, NULL, NULL, NULL, 1),
('Interactive Laser Pointer', 14.50, 40, 'TOY', 'Plastic, LED laser', NULL, NULL, NULL, NULL, 1),
('Tennis Ball Set (3 pieces)', 12.99, 85, 'TOY', 'Rubber, felt', NULL, NULL, NULL, NULL, 1),
('Cat Scratching Post', 35.50, 20, 'TOY', 'Sisal rope, wood', NULL, NULL, NULL, NULL, 1),
('Plush Elephant Toy', 13.25, 50, 'TOY', 'Soft plush fabric', NULL, NULL, NULL, NULL, 1),

-- MEDICINE với ngày hết hạn 2028
('Flea & Tick Prevention - Dogs', 65.00, 20, 'MEDICINE', NULL, '2028-01-31', 'Active ingredient: Fipronil 9.8%', '2024-01-15', 'Apply monthly', 1),
('Ear Cleaning Solution', 18.99, 35, 'MEDICINE', NULL, '2028-02-28', 'Gentle formula for pets', '2023-11-20', 'Use as needed', 1),
('Joint Support Tablets', 42.50, 30, 'MEDICINE', NULL, '2028-03-31', 'Glucosamine, Chondroitin, MSM', '2023-10-05', '1 tablet per 25 lbs body weight', 1),
('Probiotic Supplement', 28.75, 40, 'MEDICINE', NULL, '2028-04-30', 'Live beneficial bacteria cultures', '2023-08-18', '1 capsule daily', 1),
('Vitamin Supplements - Multi', 19.99, 50, 'MEDICINE', NULL, '2028-05-31', 'Essential vitamins and minerals', '2023-07-12', '1 tablet daily', 1);

-- Kiểm tra kết quả
SELECT 'All data fixed and inserted successfully!' as Message;
SELECT 
    (SELECT COUNT(*) FROM customers) as Customers,
    (SELECT COUNT(*) FROM pets) as Pets, 
    (SELECT COUNT(*) FROM products) as Products;