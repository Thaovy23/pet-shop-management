# 🐾 Petshop Management System

A complete Java Swing-based desktop application for managing a pet shop — supporting inventory, billing, user roles, customer loyalty, and reporting, with full MVC architecture.

## 📦 Features

### ✅ Core Functionalities

- **Authentication & Authorization**
  - Login/Signup for Manager and Staff
  - Role-based access control

- **Product Management**
  - Add, update, delete product (Toys, Food, Medicine)
  - Dynamic stock control with auto status handling
  - Out-of-stock products visible for restocking

- **Pet Management**
  - Add, update, mark pets as sold
  - Filter by type and price order
  - Pets are one-time purchasable items

- **Customer Management**
  - Add/edit customers with loyalty points system
  - Earn points on purchase (1 point / $10)

- **Billing System**
  - Add items to shopping cart (with stock validation)
  - Print bills as PDF invoices
  - Tracks total revenue and orders

- **Reporting Dashboard**
  - Total revenue and number of bills
  - Real-time updates via reload

## 📁 Project Structure
	src/
	├── controller/ # Business logic handlers
	├── dao/ # SQL/database access
	├── model/ # Data classes (POJOs)
	├── service/ # Business services (PDF, billing, etc.)
	├── util/ # Helpers (UI, password hash, etc.)
	├── view/ # UI components (frames, panels, dialogs)
	└── PetshopApp.java # Entry point


## 💻 Technologies Used

- Java 17
- Swing (GUI)
- MySQL
- iText PDF (for generating bills)
- JDBC (raw)
- Git for version control

## ⚙️ Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/petshop-management.git
cd petshop-management
```

### 2. Set up the Database
- Import *petshop.sql* to MySQL using your preferred tool or CLI.
- Update DB credentials in *connection_provider.java*.

### 3. Run the App
- Open in IntelliJ/NetBeans/Eclipse
- Run PetshopApp.java
- Set default user in database: admin (Manager)

####📄 Example Bill (PDF) 
*remember to change the saved directory in PdfGenerator.java*

	🐾 Pet Shop Invoice 🐾
	Date: 2024-05-16
	Customer ID: 3
	Staff ID: 2

	Items:
	[PRODUCT] Cat Food x2 - $30.00
	[PET] Shiba Inu x1 - $500.00

	Total: $530.00

## 📌 TODOs
- Pagination or search bar for large datasets
- Support for discounts/promotions
- Migrate DB config to external .properties file

## 🤝 Contributions
Pull requests and feedback welcome! Please open an issue first for discussion.

## 📜 License
This project is licensed under the MIT License — see LICENSE for details.