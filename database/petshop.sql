DROP DATABASE IF EXISTS petshop_db;
CREATE DATABASE petshop_db;
USE petshop_db;

-- === STAFF & MANAGER USERS ===
CREATE TABLE `staff` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`email` varchar(255) NOT NULL,
	`phone` varchar(20) NOT NULL,
	`username` varchar(100) NOT NULL,
	`password_hash` varchar(255) NOT NULL,
	`role` enum('MANAGER','STAFF') NOT NULL,
	`salary` decimal(10,2) NOT NULL DEFAULT '0.00',
	PRIMARY KEY (`id`),
	UNIQUE KEY `email` (`email`),
	UNIQUE KEY `username` (`username`),
	KEY `idx_staff_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- === CUSTOMERS ===
CREATE TABLE `customers` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`email` varchar(255) NOT NULL,
	`phone` varchar(20) NOT NULL,
	`loyalty_points` int NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	UNIQUE KEY `email` (`email`),
	KEY `idx_customer_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- === PETS ===
CREATE TABLE `pets` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`type` enum('DOG','CAT','HAMSTER','BIRD') NOT NULL,
	`breed` varchar(255) NOT NULL,
	`age` int NOT NULL,
	`price` decimal(10,2) NOT NULL,
	`status` tinyint DEFAULT '1',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- === PRODUCTS ===
CREATE TABLE `products` (
	`id` int NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`price` decimal(10,2) NOT NULL,
	`stock_quantity` int NOT NULL DEFAULT '0',
	`type` enum('TOY','FOOD','MEDICINE') NOT NULL,
	`material` varchar(255) DEFAULT NULL,
	`expiration_date` date DEFAULT NULL,
	`nutritional_info` text,
	`manufacture_date` date DEFAULT NULL,
	`dosage` varchar(255) DEFAULT NULL,
	`status` tinyint DEFAULT '1',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- === BILLS ===
CREATE TABLE `bills` (
	`id` int NOT NULL AUTO_INCREMENT,
	`customer_id` int NOT NULL,
	`staff_id` int NOT NULL,
	`total_amount` decimal(10,2) NOT NULL,
	`payment_method` enum('CASH','CARD') NOT NULL,
	`transaction_time` datetime DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	KEY `customer_id` (`customer_id`),
	KEY `staff_id` (`staff_id`),
	CONSTRAINT `bills_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`),
	CONSTRAINT `bills_ibfk_2` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- === BILL ITEMS ===
CREATE TABLE `bill_items` (
	`id` int NOT NULL AUTO_INCREMENT,
	`bill_id` int NOT NULL,
	`item_type` enum('PET','PRODUCT') NOT NULL,
	`pet_id` int DEFAULT NULL,
	`product_id` int DEFAULT NULL,
	`quantity` int NOT NULL,
	`unit_price` decimal(10,2) NOT NULL,
	PRIMARY KEY (`id`),
	KEY `bill_id` (`bill_id`),
	KEY `pet_id` (`pet_id`),
	KEY `product_id` (`product_id`),
	CONSTRAINT `bill_items_ibfk_1` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`),
	CONSTRAINT `bill_items_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`),
	CONSTRAINT `bill_items_ibfk_3` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;