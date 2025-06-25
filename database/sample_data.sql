-- ===== SAMPLE DATA FOR PETSHOP DATABASE =====
-- Run this after creating the main database schema
-- All data is in English as requested

USE petshop_db;

-- ===== INSERT CUSTOMERS =====
INSERT INTO `customers` (`name`, `email`, `phone`, `loyalty_points`) VALUES
('John Smith', 'john.smith@email.com', '555-0101', 150),
('Emily Johnson', 'emily.johnson@email.com', '555-0102', 200),
('Michael Brown', 'michael.brown@email.com', '555-0103', 75),
('Sarah Davis', 'sarah.davis@email.com', '555-0104', 300),
('David Wilson', 'david.wilson@email.com', '555-0105', 50),
('Lisa Anderson', 'lisa.anderson@email.com', '555-0106', 175),
('James Miller', 'james.miller@email.com', '555-0107', 125),
('Jessica Taylor', 'jessica.taylor@email.com', '555-0108', 250),
('Robert Garcia', 'robert.garcia@email.com', '555-0109', 100),
('Amanda Rodriguez', 'amanda.rodriguez@email.com', '555-0110', 325),
('Christopher Lee', 'christopher.lee@email.com', '555-0111', 90),
('Michelle White', 'michelle.white@email.com', '555-0112', 400),
('Daniel Martinez', 'daniel.martinez@email.com', '555-0113', 60),
('Ashley Thompson', 'ashley.thompson@email.com', '555-0114', 180),
('Matthew Moore', 'matthew.moore@email.com', '555-0115', 220);

-- ===== INSERT PETS =====
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
('Nala', 'CAT', 'Ragdoll', 1, 650.00, 1),

-- Birds
('Sunny', 'BIRD', 'Canary', 1, 150.00, 1),
('Polly', 'BIRD', 'Cockatiel', 2, 200.00, 1),
('Rio', 'BIRD', 'Parakeet', 1, 120.00, 1),
('Kiwi', 'BIRD', 'Lovebird', 1, 180.00, 1),
('Mango', 'BIRD', 'Conure', 2, 300.00, 1),

-- Hamsters
('Nibbles', 'HAMSTER', 'Syrian', 1, 25.00, 1),
('Peanut', 'HAMSTER', 'Dwarf', 1, 20.00, 1),
('Squeaky', 'HAMSTER', 'Roborovski', 1, 30.00, 1),
('Fluffy', 'HAMSTER', 'Chinese', 1, 22.00, 1),
('Patches', 'HAMSTER', 'Russian', 1, 28.00, 1);

-- ===== INSERT PRODUCTS =====

-- Pet Food Products
INSERT INTO `products` (`name`, `price`, `stock_quantity`, `type`, `material`, `expiration_date`, `nutritional_info`, `manufacture_date`, `dosage`, `status`) VALUES
('Premium Dog Food - Chicken & Rice', 45.99, 50, 'FOOD', NULL, '2026-06-15', 'Protein 26%, Fat 16%, Fiber 4%, Moisture 10%', '2024-06-15', NULL, 1),
('Kitten Formula - Salmon Flavor', 32.50, 35, 'FOOD', NULL, '2026-04-20', 'Protein 32%, Fat 20%, Fiber 3%, Moisture 12%', '2024-04-20', NULL, 1),
('Adult Cat Food - Tuna & Vegetables', 28.75, 40, 'FOOD', NULL, '2026-08-10', 'Protein 28%, Fat 14%, Fiber 3.5%, Moisture 10%', '2024-08-10', NULL, 1),
('Large Breed Puppy Food', 52.00, 25, 'FOOD', NULL, '2026-07-22', 'Protein 28%, Fat 18%, Fiber 4%, Moisture 10%', '2024-07-22', NULL, 1),
('Senior Dog Food - Joint Care', 38.90, 30, 'FOOD', NULL, '2026-05-30', 'Protein 24%, Fat 12%, Fiber 4.5%, Moisture 10%', '2024-05-30', NULL, 1),
('Bird Seed Mix - Premium Blend', 15.25, 60, 'FOOD', NULL, '2026-12-01', 'Sunflower seeds, millet, safflower, dried fruits', '2024-12-01', NULL, 1),
('Hamster Food - Complete Nutrition', 12.99, 45, 'FOOD', NULL, '2026-09-15', 'Seeds, grains, dried vegetables, vitamins', '2024-09-15', NULL, 1),
('Organic Dog Treats - Beef Flavor', 18.50, 75, 'FOOD', NULL, '2026-03-28', 'Organic beef, sweet potato, natural preservatives', '2024-03-28', NULL, 1),

-- Pet Toys
('Rope Toy - Cotton Braided', 8.99, 80, 'TOY', 'Cotton rope', NULL, NULL, NULL, NULL, 1),
('Interactive Puzzle Ball', 15.75, 45, 'TOY', 'Durable plastic', NULL, NULL, NULL, NULL, 1),
('Feather Wand Cat Toy', 12.50, 60, 'TOY', 'Plastic handle, natural feathers', NULL, NULL, NULL, NULL, 1),
('Squeaky Duck Toy', 6.25, 100, 'TOY', 'Rubber', NULL, NULL, NULL, NULL, 1),
('Catnip Mouse Set (3 pieces)', 9.99, 70, 'TOY', 'Fabric, catnip filling', NULL, NULL, NULL, NULL, 1),
('Dental Chew Bone - Large', 11.75, 55, 'TOY', 'Nylon', NULL, NULL, NULL, NULL, 1),
('Interactive Laser Pointer', 14.50, 40, 'TOY', 'Plastic, LED laser', NULL, NULL, NULL, NULL, 1),
('Hamster Exercise Wheel', 22.00, 25, 'TOY', 'Metal frame, plastic wheel', NULL, NULL, NULL, NULL, 1),
('Bird Swing Perch', 16.75, 35, 'TOY', 'Natural wood, cotton rope', NULL, NULL, NULL, NULL, 1),
('Plush Elephant Toy', 13.25, 50, 'TOY', 'Soft plush fabric', NULL, NULL, NULL, NULL, 1),

-- Pet Medicine
('Flea & Tick Prevention - Dogs', 65.00, 20, 'MEDICINE', NULL, '2027-01-15', 'Active ingredient: Fipronil 9.8%', '2024-01-15', 'Apply monthly', 1),
('Ear Cleaning Solution', 18.99, 35, 'MEDICINE', NULL, '2026-11-20', 'Gentle formula for pets', '2023-11-20', 'Use as needed', 1),
('Joint Support Tablets', 42.50, 30, 'MEDICINE', NULL, '2026-10-05', 'Glucosamine, Chondroitin, MSM', '2023-10-05', '1 tablet per 25 lbs body weight', 1),
('Probiotic Supplement', 28.75, 40, 'MEDICINE', NULL, '2026-08-18', 'Live beneficial bacteria cultures', '2023-08-18', '1 capsule daily', 1),
('Wound Care Spray', 15.50, 45, 'MEDICINE', NULL, '2026-12-30', 'Antiseptic formula', '2023-12-30', 'Spray 2-3 times daily', 1),
('Digestive Aid Powder', 24.99, 25, 'MEDICINE', NULL, '2026-06-25', 'Digestive enzymes and prebiotics', '2023-06-25', '1 tsp per meal', 1),
('Calming Tablets for Anxiety', 35.00, 30, 'MEDICINE', NULL, '2026-09-10', 'Natural calming ingredients', '2023-09-10', '1 tablet twice daily', 1),
('Vitamin Supplements - Multi', 19.99, 50, 'MEDICINE', NULL, '2026-07-12', 'Essential vitamins and minerals', '2023-07-12', '1 tablet daily', 1),
('Eye Drops - Gentle Formula', 12.75, 60, 'MEDICINE', NULL, '2026-04-08', 'Sterile eye solution', '2023-04-08', '1-2 drops as needed', 1),
('Deworming Tablets', 22.50, 35, 'MEDICINE', NULL, '2026-05-14', 'Broad spectrum dewormer', '2023-05-14', 'Follow vet instructions', 1);

-- Display success message
SELECT 'Sample data inserted successfully!' as Message;
SELECT 
    (SELECT COUNT(*) FROM customers) as 'Customers Added',
    (SELECT COUNT(*) FROM pets) as 'Pets Added', 
    (SELECT COUNT(*) FROM products) as 'Products Added'; 